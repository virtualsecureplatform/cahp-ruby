import chisel3._

class RamPort(implicit val conf:CAHPConfig) extends Bundle {
  val readData = Input(UInt(conf.ramDataWidth.W))

  val addr = Output(UInt(conf.ramAddrWidth.W))
  val writeData = Output(UInt(conf.ramDataWidth.W))
  val writeEnable = Output(Bool())

  override def cloneType: this.type = new RamPort()(conf).asInstanceOf[this.type]
}

class RomPort(implicit val conf:CAHPConfig) extends Bundle {
  val data = Input(UInt(conf.romDataWidth.W))

  val addr = Output(UInt(conf.romAddrWidth.W))

  override def cloneType: this.type = new RomPort()(conf).asInstanceOf[this.type]
}

class MainRegisterOutPort(implicit val conf:CAHPConfig) extends Bundle{
  val x0 = Output(UInt(16.W))
  val x1 = Output(UInt(16.W))
  val x2 = Output(UInt(16.W))
  val x3 = Output(UInt(16.W))
  val x4 = Output(UInt(16.W))
  val x5 = Output(UInt(16.W))
  val x6 = Output(UInt(16.W))
  val x7 = Output(UInt(16.W))
  val x8 = Output(UInt(16.W))
  val x9 = Output(UInt(16.W))
  val x10 = Output(UInt(16.W))
  val x11 = Output(UInt(16.W))
  val x12 = Output(UInt(16.W))
  val x13 = Output(UInt(16.W))
  val x14 = Output(UInt(16.W))
  val x15 = Output(UInt(16.W))
}
