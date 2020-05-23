case class CAHPConfig() {
  var debugIf = true
  var debugId = true
  var debugEx = true
  var debugMem = true
  var debugWb = true

  var test = false
  var testRom:Seq[BigInt] = Seq(BigInt(0))
  var testRam:Seq[BigInt] = Seq(BigInt(0))

  //IF Unit
  val instAddrWidth = 9
  val instDataWidth = 24

  val romAddrWidth = 7
  val romDataWidth = 32

  val dataAddrWidth = 9
  val ramAddrWidth = 8
  val ramDataWidth = 16
}