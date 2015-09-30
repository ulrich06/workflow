package fr.unice.modalis.cosmic.deposit.logic.guard.predicate

import fr.unice.modalis.cosmic.deposit.logic.guard.GuardAction

/**
 * Created by cyrilcecchinel on 28/04/2014.
 */
case class ORPredicate(left: GuardAction, right: GuardAction) extends Predicate {
  override def toString: String = left + " OR " + right
}
