package fr.unice.modalis.cosmic.deployment.network.dsl.kernel

import fr.unice.modalis.cosmic.deployment.network.dsl.kernel.CommunicationType.CommunicationType
import fr.unice.modalis.cosmic.deployment.network.dsl.kernel.CommunicationWay.CommunicationWay
import fr.unice.modalis.cosmic.deployment.network.dsl.kernel.EntityComputation.EntityComputation
import fr.unice.modalis.cosmic.deployment.network.dsl.kernel.EntityPower.EntityPower
import fr.unice.modalis.cosmic.deployment.network.dsl.kernel.EntityType.EntityType
import fr.unice.modalis.cosmic.deployment.network.dsl.kernel.Media.Media
import fr.unice.modalis.cosmic.deployment.network.dsl.kernel.ProgrammingLanguage.ProgrammingLanguage
import fr.unice.modalis.cosmic.deposit.core.Properties
/**
 * Represent sensing infrastructure nodes
 * Created by Cyril Cecchinel - I3S Laboratory on 13/05/15.
 */



trait GenericNode extends Properties{
  def isProgrammable:Boolean = readProperty("programmable").getOrElse(false).asInstanceOf[Boolean]
  val name:String
}


case class Entity(name:String,
                  sensors:Set[Sensor],
                  power:EntityPower,
                  communication:Set[Communication],
                  eType:EntityType,
                  language:ProgrammingLanguage,
                  computation:EntityComputation) extends GenericNode


object EntityPower extends Enumeration {
  type EntityPower = Value
  val MAINS, BATTERY = Value
}

case class Communication(cType:CommunicationType, cWay:CommunicationWay)

object ProgrammingLanguage extends Enumeration {
  type ProgrammingLanguage = Value
  val nesC,Contiki,Python,Processing,Java,Groovy = Value
}
object CommunicationType extends Enumeration {
  type CommunicationType = Value
  val XBEE, ZWave, WiFi, Serial, USB, WAN = Value
}

object CommunicationWay extends Enumeration {
  type CommunicationWay = Value
  val IN, OUT = Value
}

object EntityType extends Enumeration {
  type EntityType = Value
  val Arduino, Raspberry, CubieBoard, Server, Misc = Value
}

object EntityComputation extends Enumeration {
  type EntityComputation = Value
  val HIGH, LOW, CLOUD = Value
}




case class Node(name:String) extends GenericNode
case class Sensor(name:String) extends GenericNode
case class Bridge(name:String) extends GenericNode
case class SensorPlatform(name:String) extends GenericNode
case class Remote(name: String) extends GenericNode
case class Repeater(name:String) extends GenericNode
case class Edge(source: GenericNode, destination:GenericNode, media:Media = Media.Unknown)

object Media extends Enumeration {
  type Media = Value
  val XBee, Serial, ZWave, USB, Unknown = Value
}
