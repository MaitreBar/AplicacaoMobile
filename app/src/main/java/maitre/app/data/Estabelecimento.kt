package maitre.app.data

import java.io.Serializable

data class Estabelecimento(
    val idEstabelecimento: Int,
    val nome: String,
    val senha: String,
    val logradouro: String,
    val numero: String,
    val complemento: String,
    val cep: String,
    val diasDaSemana: String,
    val faixaDePreco: String,
    val cnpj: String,
    val telefoneContato: String,
    val horarioAbertura: String,
    val horarioFechamento: String,
    val descricao: String,
    val email: String,
    val assentos: List<Assento>,
    val tags: String,
    val reservas: List<Reserva>
) : Serializable