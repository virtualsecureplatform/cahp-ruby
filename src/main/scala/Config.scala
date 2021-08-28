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
  val romAddrWidth = 8
  val romDataWidth = 32

  val instAddrWidth = romAddrWidth+2
  val instDataWidth = 24

  val ramAddrWidth = 9
  val ramDataWidth = 16

  val dataAddrWidth = ramAddrWidth+1
}