package fr.unice.modalis.cosmic.demos

import fr.unice.modalis.cosmic.deployment.PreDeploy
import fr.unice.modalis.cosmic.deployment.utils.TopologyModelBuilder
import fr.unice.modalis.cosmic.deposit.algo.ExtendPolicy
import fr.unice.modalis.cosmic.deposit.core._
import fr.unice.modalis.cosmic.deposit.dsl.DEPOSIT

/**
  * Created by Cyril Cecchinel - I3S Laboratory on 16/03/2017.
  */

case class AlertMessageType(value:(StringType, StringType, LongType) = (StringType(""),StringType(""), LongType(0L))) extends SensorDataType{
  def this(value:(String, String)) = this((StringType(value._1), StringType(value._2), LongType(0L)))

  override def getNameField: Field = Field("alert", bindings("alert"))

  override def getTimeField: Field = Field("time", bindings("time"))

  override def getObservationField: Field = Field("alert", bindings("alert"))

  override def getNullValue: SensorDataType = AlertMessageType()

  override val bindings: Map[String, Class[_ <: AtomicType]] = Map("alert" -> classOf[StringType], "room" -> classOf[StringType], "time" -> classOf[LongType])

  override val name: String = "AlertMessageType"

}

case class TemperatureSensorDataType(value:(StringType, DoubleType, LongType) = (StringType(""),DoubleType(0.0), LongType(0L))) extends SensorDataType{
  def this(value:(String, Double)) = this((StringType(value._1), DoubleType(value._2), LongType(0L)))

  override def getNameField: Field = Field("name", bindings("name"))

  override def getTimeField: Field = Field("time", bindings("time"))

  override def getObservationField: Field = Field("value", bindings("value"))

  override def getNullValue: SensorDataType = TemperatureSensorDataType()

  override val bindings: Map[String, Class[_ <: AtomicType]] = Map("name" -> classOf[StringType], "value" -> classOf[DoubleType], "time" -> classOf[LongType])

  override val name: String = "AlertMessageType"

}

object ACStatusPolicy extends DEPOSIT {
  this hasForName "ACConverter"
  this handles classOf[TemperatureSensorDataType]

  val ac = declare aGenericInput() named "AC"
  val cond1 = define aCondition "v < 18"
  val cond2 = define aCondition "v > 25"

  val cool = declare aGenericOutput() named "COOL"
  val heat = declare aGenericOutput() named "HEAT"
  val off = declare aGenericOutput() named "OFF"

  flows {
    ac() -> cond1("input")
    cond1("then") -> cool()
    cond1("else") -> cond2("input")
    cond2("then") -> heat()
    cond2("else") -> off()
  }
}

object ThesisDemo extends DEPOSIT{

  this hasForName "ThermalShockPrevention"
  this handles classOf[TemperatureSensorDataType]

  val r1_t1 = declare aPeriodicSensor() withPeriod 300 named "R1_T1"
  val r1_t2 = declare aPeriodicSensor() withPeriod 300 named "R1_T2"
  val outside_t = declare aPeriodicSensor() withPeriod 3600 named "OUTSIDE_T"
  val r1_ac = declare aPeriodicSensor() withPeriod 60 named "R1_AC"
  val collector = declare aCollector() named "COLLECTOR"

  val avg = define anAvg() withInputs("i1", "i2")
  val sub = define aSubstractor() withInputs("i1", "i2")
  val abs = define anAbsoluteValue()
  val condition = define aCondition "v > 8"
  val produce = define aProducer new AlertMessageType("ALERT_TEMP", "R_1") withInputs("i1", "i2")
  val process = define aProcess ACStatusPolicy()

  flows {
    r1_t1() -> avg("i1")
    r1_t2() -> avg("i2")
    avg() -> sub("i1")
    outside_t() -> sub("i2")
    sub () -> abs ()
    abs() -> condition()
    r1_ac () -> process ("AC")
    condition("then") -> produce("i1")
    process("COOL") -> produce("i2")
    produce() -> collector()
  }
}

object Application extends App{
  val infra = TopologyModelBuilder("assets/configurations/TheseDemo.xml")
  //ThesisDemo().exportToGraphviz()
  val policyPreDeployed = PreDeploy(ThesisDemo(),infra)
  //policyPreDeployed.exportToGraphviz()
  ExtendPolicy(policyPreDeployed).exportToGraphviz()
  //val deployed = Deploy(policyPreDeployed, infra, DeploymentRepartition.CLOSEST_TO_SENSORS)

  //deployed.foreach(_.exportToGraphviz())
//  println(CodeGenerator.orderedGenerationList(policyPreDeployed).map {_.commonName})
  //println(infra.orderedTopology.map{_.name})

}
