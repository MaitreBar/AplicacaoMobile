package maitre.app.layout

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import maitre.app.R
import maitre.app.databinding.FragmentChatbotBinding

class ChatbotFragment : Fragment() {

    lateinit var binding: FragmentChatbotBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        binding = FragmentChatbotBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSend.setOnClickListener{



            var valorUsuario = binding.etInput.text.toString()
            val containerChat = binding.containerChat

            textViewRespostaUsuario(valorUsuario)

            if (containerChat.childCount > 0) {
                val ultimoItem = containerChat.getChildAt(containerChat.childCount - 4)

                if (ultimoItem is TextView) {
                    val texto = ultimoItem.text.toString()
                    if (texto == "Tem certeza que deseja encerrar o chamado?") {
                        when (valorUsuario) {
                            "1" -> {
                                textViewRespostaChat("Muito obrigado! Até a proxima e nos deixe uma avaliação.")
                                textViewRespostaChat("Chamado Encerrado")
                                return@setOnClickListener
                            }

                            "2" -> {
                                textViewRespostaChat("Digite uma opção para eu poder te ajudar!")
                                return@setOnClickListener
                            }

                            else -> {
                                textViewRespostaChat("Opção inválida. Por favor, escolha uma opção válida.")
                                return@setOnClickListener
                            }
                        }
                    }
                }
                // Limpar o EditText após enviar a resposta
                binding.etInput.setText("")
            }


            when (valorUsuario) {

                "1" -> {
                    textViewRespostaChat("O projeto funciona da seguinte maneira: É um App que facilita a sua reserva, navegando pelo app você encontre os restaurantes parceiros e você pode reservar sua mesa e sua cadeira no horario que você deseja, facilitando tudo isso na palma da sua mão. Se tiver mais dúvidas, sinta-se à vontade para perguntar.")
                }

                "2" -> {
                    textViewRespostaChat("Se você gostaria de fazer uma avaliação ou crítica, por favor, acesse a tela de feedback.")
                }

                "3" -> {
                    textViewRespostaChat("Você está com problemas no e-mail ou senha. Por favor, entre em contato com nosso suporte via e-mail ou WhatsApp.")
                    textViewRespostaChat("https://w.app/ABGpHD")
                }

                "4" -> {
                    textViewRespostaChat("Tem certeza que deseja encerrar o chamado?")
                    textViewRespostaChat("1. Sim")
                    textViewRespostaChat("2. Não")


                }

                else -> {
                    textViewRespostaChat("Opção inválida. Por favor, escolha uma opção válida.")

                }
            }

        }

        view.findViewById<ImageButton>(R.id.btn_voltar_perfil).setOnClickListener {
            (activity as MainActivity).replaceFragment(Perfil())
            (activity as MainActivity).showBottomNavigationView()
        }
    }

    fun converterDp(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun textViewRespostaChat(texto: String) {
        val params = LinearLayout.LayoutParams(
            converterDp(270),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val textView = TextView(context)
        textView.text = texto
        textView.layoutParams = params
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.rouded_chatbox)
        drawable?.setTint(ContextCompat.getColor(requireContext(), R.color.cinzaclaro))
        params.setMargins(0, 0, 0, converterDp(5))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        textView.setPadding(
            converterDp(12),
            converterDp(12),
            converterDp(12),
            converterDp(12)
        )
        textView.background = drawable
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.preto))
        textView.setTypeface(Typeface.create("quicksand_semibold", Typeface.NORMAL))


        Linkify.addLinks(textView, Linkify.WEB_URLS)


        textView.setOnClickListener {
            val url = textView.text.toString()
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }


        binding.containerChat.addView(textView)

        val scrollView = view?.findViewById<ScrollView>(R.id.scrollView)
        scrollView?.fullScroll(View.FOCUS_DOWN)
    }

    fun textViewRespostaUsuario(texto: String) {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val textView = TextView(context)
        textView.text = texto
        textView.layoutParams = params
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.rouded_resposta_box)
        drawable?.setTint(ContextCompat.getColor(requireContext(), R.color.begeClaro))
        params.setMargins(converterDp(125), 0, 0, converterDp(5))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        textView.gravity = Gravity.END
        params.gravity = Gravity.END
        textView.setPadding(
            converterDp(30),
            converterDp(12),
            converterDp(15),
            converterDp(12)
        )
        textView.background = drawable
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.branco))
        textView.setTypeface(Typeface.create("quicksand_semibold", Typeface.NORMAL))

        binding.containerChat.addView(textView)

        val scrollView = view?.findViewById<ScrollView>(R.id.scrollView)
        scrollView?.fullScroll(View.FOCUS_DOWN)

    }

}