package fr.unice.modalis.cosmic.workflow.algo

import fr.unice.modalis.cosmic.workflow.core._

import scala.collection.mutable.ArrayBuffer

/**
 * Created by Cyril Cecchinel - I3S Laboratory on 28/04/15.
 */
case class Unification[T<:DataType](val a:JoinPointOutput[T], val b:JoinPointInput[T])

object ExtendPolicy {


  def apply(p:Policy):Policy = {
    val toApply = (for (activity <- p.operations; if(activity.isExtendable)) yield generateJoinPointsForOperation(activity))
      .foldLeft(Set[JoinPoint[_ <:DataType]](),Set[Link[_ <:DataType]]()){(acc, e) => (acc._1 ++ e._1, acc._2 ++ e._2)}
    var policy = p
    toApply._1.foreach(j => policy = policy.add(j))
    toApply._2.foreach(l => policy = policy.addLink(l))

    policy
  }

  /**
   * Compute the join points for an operation
   * Pre-condition: The operation can be joined
   * @param o Operation
   * @tparam T Inputs type
   * @tparam O Outputs type
   * @return List of join points and List of links
   */
  def generateJoinPointsForOperation[T<:DataType, O<:DataType](o:Operation[T,O]) = {
    require(o.isExtendable)
    val linksToAdd = new ArrayBuffer[Link[_<:DataType]]()
    val iosToAdd = new ArrayBuffer[JoinPoint[_<:DataType]]()

    o.outputs.foreach(o => {
      val l = new Link(o, new JoinPointOutput(o).input)
      val s = l.destination
      linksToAdd += l
      iosToAdd += s.asInstanceOf[JoinPointOutput[_<:DataType]]
    })

    o.inputs.foreach(i => {
      val l = new Link(new JoinPointInput(i).output, i)
      val s = l.source
      linksToAdd += l
      iosToAdd += s.asInstanceOf[JoinPointInput[_<:DataType]]
    })

    (iosToAdd.toSet, linksToAdd.toSet)

  }

}

object FactorizePolicy {

  def apply(p:Policy) = deleteJoinPointsForOperation(p)

  /**
   * Remove all join points in a policy
   * @param p Input policy
   * @return A new policy without join points
   */
  def deleteJoinPointsForOperation(p:Policy) = {
    var policy = p
    p.ios.collect( {case x:JoinPoint[_] => x}).foreach(e => policy = policy.deleteIO(e))
    policy
  }
}


object Weave {

  def apply(p1: Policy, p2: Policy, associations:Set[Unification[_<:DataType]]) = {
    if (!p1.isExtendable) throw new NotExtendableException(p1)
    if (!p2.isExtendable) throw new NotExtendableException(p2)

    val links = for (x<-associations) yield createLink(x)
    var newPolicy = p1 ++ p2
    for (l <- links) yield newPolicy = newPolicy.addLink(l)
    FactorizePolicy(newPolicy)
  }

  def createLink[T<:DataType](u:Unification[T]) = {
    new Link[T](u.a.fromConceptOutput, u.b.toConceptInput)
  }

}