import chisel3._

class InstructionDecoderPort(implicit val conf:CAHPConfig) extends Bundle {
  val inst = Input(UInt(24.W))

  val imm = Output(UInt(16.W))
  val pcImm = Output(UInt(16.W))
  val pcImmSel = Output(Bool())
  val regRead = Output(new MainRegInRead)
  val longInst = Output(Bool())
  val inASel = Output(Bool())
  val inBSel = Output(Bool())

  val exOut = Output(new ExUnitIn)
  val memOut = Output(new MemUnitIn)
  val wbOut = Output(new WbUnitIn)

  val testImmType = if(conf.test) Output(UInt(4.W)) else Output(UInt(0.W))
  val testPCImmType = if(conf.test) Output(UInt(2.W)) else Output(UInt(0.W))
}

class InstructionDecoder(implicit val conf:CAHPConfig) extends Module {
  val io = IO(new InstructionDecoderPort)

  io.imm := DecoderUtils.genImm(io.inst, DecoderUtils.getImmType(io.inst))
  io.pcImm := DecoderUtils.genPCImm(io.inst, DecoderUtils.getPCImmType(io.inst))
  io.pcImmSel := DecoderUtils.getPCImmSel(io.inst)
  io.testImmType := DecoderUtils.getImmType(io.inst)
  io.testPCImmType := DecoderUtils.getPCImmType(io.inst)

  io.inASel := DecoderUtils.getInASel(io.inst)
  io.inBSel := DecoderUtils.getInBSel(io.inst)
  io.exOut.aluIn.opcode := DecoderUtils.getExOpcode(io.inst)
  io.exOut.aluIn.inA := DontCare
  io.exOut.aluIn.inB := DontCare
  io.exOut.bcIn.pcOpcode := DecoderUtils.getPCOpcode(io.inst)
  io.exOut.bcIn.pcImm := DontCare
  io.exOut.bcIn.pc := DontCare
  io.exOut.bcIn.pcAdd := DontCare
  io.memOut.read := DecoderUtils.getMemRead(io.inst)
  io.memOut.write := DecoderUtils.getMemWrite(io.inst)
  io.memOut.byte := DecoderUtils.getMemByte(io.inst)
  io.memOut.signExt := DecoderUtils.getMemSignExt(io.inst)
  io.memOut.data := DontCare
  io.memOut.data := DontCare

  io.longInst := (io.inst(0) === 1.U)

  when(io.longInst) {
    io.regRead.rs1 := io.inst(15, 12)
    when(io.inst(2, 1) === InstructionCategory.InstM || io.inst(2, 1) === InstructionCategory.InstJ){
      io.regRead.rs2 := io.inst(11, 8)
    }.otherwise{
      io.regRead.rs2 := io.inst(19,16)
    }
  }.otherwise{
    when(io.inst(2, 1) === InstructionCategory.InstM){
      io.regRead.rs1 := 1.U(4.W)
      io.regRead.rs2 := io.inst(11, 8)
    }.otherwise{
      io.regRead.rs1 := io.inst(11, 8)
      io.regRead.rs2 := io.inst(15, 12)
    }
  }

  when(io.inst(2,1) === InstructionCategory.InstJ){
    io.wbOut.regWrite.rd := 0.U(4.W)
  }.otherwise{
    io.wbOut.regWrite.rd := io.inst(11, 8)
  }
  io.wbOut.regWrite.writeEnable := DecoderUtils.getRegWrite(io.inst)
  io.wbOut.regWrite.writeData := DontCare
  io.wbOut.inst := DontCare
  io.wbOut.instAddr := DontCare

  io.wbOut.finishFlag := (io.inst(15,0) === 0xE.U)
}
