package fr.unice.modalis.cosmic.actions.guard.constraint

import fr.unice.modalis.cosmic.workflow.core.DataType


/**
 * Value Constraint
 * Fix a constraint upon a value  Value op threshold
 * @constructor Create a new value constraint
 * @param threshold Threshold
 * @param operator Operator
 */
case class ValueConstraint(operator: String, threshold:DataType) extends Constraint {

  def isCorrect(o: String): Boolean = {
    o == ">" || o == "<" || o == "==" || o == ">=" || o == "<=" || o == "!="
  }

  if (!isCorrect(operator)) throw new Exception("Bad operator for " + operator)

  override def toString(): String = "\\" + operator + threshold.value


}
