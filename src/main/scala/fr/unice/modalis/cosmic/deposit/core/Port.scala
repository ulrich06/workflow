package fr.unice.modalis.cosmic.deposit.core


/**
 * Component IO trait : represent a component IO interface
 * @tparam T Supported datatype
 */
trait Port[+T <: DataType] {
  val parent:Concept
  var name:String
  def setName(n: String) = name = n // /!\ Mutable field, PoC Only

  val id = parent.id + "_" + name
}

/**
 * Workflow component input
 * @tparam T Data type
 */
case class Input[+T <: DataType](var name:String, parent:Concept) extends Port[T] {

  def this(parent:Concept) = this("input_" + scala.util.Random.alphanumeric.take(5).mkString, parent)
}

/**
 * Workflow component output
 * @tparam T Data type
 */
case class Output[+T <: DataType](var name:String, parent:Concept) extends Port[T] {
  def this(parent:Concept) = this("output_" + scala.util.Random.alphanumeric.take(5).mkString, parent)

}
