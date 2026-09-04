case class CAHPConfig(
  romAddrWidth: Int = 8,
  ramAddrWidth: Int = 9,
) {
  require(romAddrWidth > 0, "ROM address width must be positive")
  require(ramAddrWidth > 0, "RAM address width must be positive")

  var debugIf = true
  var debugId = true
  var debugEx = true
  var debugMem = true
  var debugWb = true

  var test = false
  var testRom:Seq[BigInt] = Seq(BigInt(0))
  var testRam:Seq[BigInt] = Seq(BigInt(0))

  //IF Unit
  val romDataWidth = 32

  val instAddrWidth = romAddrWidth+2
  val instDataWidth = 24

  val ramDataWidth = 16

  val dataAddrWidth = ramAddrWidth+1
}
