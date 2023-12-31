package maitre.app.layout

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import maitre.app.R
import maitre.app.data.Assento
import maitre.app.data.Estabelecimento
import maitre.app.data.Reserva
import maitre.app.databinding.FragmentCriacaoReservaBinding
import maitre.app.utils.NetworkUtils
import maitre.app.utils.Sessao
import maitre.app.utils.Sessao.reserva
import maitre.app.utils.Sessao.urlApi
import maitre.app.utils.Sessao.usuario
import maitre.app.utils.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CriacaoReserva : Fragment() {

    lateinit var binding: FragmentCriacaoReservaBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var e : Estabelecimento
//    Variáveis do Spinner
    private val assentosId = mutableListOf<String>()
    private var spinnerLanguages: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
    }
  
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCriacaoReservaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        e = Sessao.estabelecimento!!

        val fragmentManager = activity?.supportFragmentManager!!
        val fragmentTransaction = fragmentManager.beginTransaction()

        binding.btnReservaNome.hint = e.nome

        binding.btnReservaNome.setOnClickListener {
            Sessao.estabelecimento = e
            (activity as MainActivity).replaceFragment(VisaoEstabelecimento())
        }

        fun timeToMinutes(time: String): Int {
            // Split the time string into components (hours, minutes, seconds.milliseconds)
            val components = time.split(":").flatMap { it.split(".") }.map { it.toInt() }

            // Extract hours, minutes, and seconds
            val hours = components[0]
            val minutes = if (components.size > 1) components[1] else 0
            val seconds = if (components.size > 2) components[2] else 0

            // Calculate total minutes
            return hours * 60 + minutes + seconds / 60
        }

        fun generateDayButtons() {
            val startDate = LocalDate.now()

            for (i in 0 until 30) {
                val args = Bundle()
                val day = startDate.plusDays(i.toLong())
                val diaSemana = day.format(DateTimeFormatter.ofPattern("E"))
                val diaMes = day.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                args.putString("diaSemana", diaSemana)
                args.putString("diaMes", diaMes)

                fragmentTransaction.add(
                    R.id.container_reserva_dias,
                    CardDiaReservaFragment::class.java,
                    args
                )
            }
        }

        generateDayButtons()

        val startMinutes = timeToMinutes(e.horarioAbertura)
        val endMinutes = timeToMinutes(e.horarioFechamento)

        val timeArray = mutableListOf<String>()


        fun generateTimeArray() {
            val newArray = mutableListOf<String>()
            for (minutes in startMinutes..endMinutes step 30) {
                val hours = minutes / 60
                val mins = minutes % 60
                val time = "${String.format("%02d", hours)}:${String.format("%02d", mins)}"
                newArray.add(time)
            }
            timeArray.clear()
            timeArray.addAll(newArray)
        }

        generateTimeArray()

        timeArray.forEach {horario ->
            val args = Bundle()
            args.putString("hora", horario)

            fragmentTransaction.add(
                R.id.gl_horario_reserva,
                CardHorario::class.java,
                args
            )

        }
        var assentoSelecionado: String? = null

        binding.spinnerSample.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Retrieve the selected item
                assentoSelecionado = assentosId[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                assentoSelecionado = null
            }
        }

        binding.btConfirmarReserva.setOnClickListener {
            val assentos : MutableList<Assento> = mutableListOf()

            NetworkUtils.getRetrofitInstance(urlApi)
                .getAssentoById(assentoSelecionado!!.toInt()).enqueue(object : Callback<Assento>{
                    override fun onResponse(call: Call<Assento>, response: Response<Assento>) {
                        if(response.isSuccessful) {
                            assentos.add(response.body()!!)
                        }
                    }

                    override fun onFailure(call: Call<Assento>, t: Throwable) {
                        Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                    }
                })

            val novaReserva = Reserva(
                null,
                LocalDate.parse(sharedViewModel.dia, DateTimeFormatter.ofPattern("dd/MM/yyyy")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString(),
                LocalTime.parse(sharedViewModel.hora).format(DateTimeFormatter.ofPattern("HH:mm")).toString(),
                false,
                null,
                false,
                null,
                null,
                e,
                usuario!!,
                assentos
                )
            sharedViewModel.novaReserva = novaReserva

            (activity as MainActivity).replaceFragment(ConfirmacaoReserva())
        }

        fragmentTransaction.commit()

//        Código Spinner
        spinnerLanguages = view.findViewById(R.id.spinner_sample)

        assentosId.add(getString(R.string.selecione_assento))
        e.assentos.forEach { assento ->
            if (assento.disponivel) {
            assentosId.add(assento.id)
            }
        }

        val aa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, assentosId)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguages?.adapter = aa
    }
}