import chisel3._
import chisel3.util.{BitPat, Cat}

object ALUOpcode {
  def ADD = "b0000".U(4.W)
  def SUB = "b0001".U(4.W)
  def AND = "b0010".U(4.W)
  def XOR = "b0011".U(4.W)
  def OR  = "b0100".U(4.W)
  def LSL = "b0101".U(4.W)
  def LSR = "b0110".U(4.W)
  def ASR = "b0111".U(4.W)
  def MOV = "b1000".U(4.W)
}

class ALUPortIn(implicit val conf:CAHPConfig) extends Bundle {
  val inA = UInt(16.W)
  val inB = UInt(16.W)
  val opcode = UInt(4.W)
}

class ALUPortOut(implicit val conf:CAHPConfig) extends Bundle {
  val out = Output(UInt(16.W))
  val flagCarry = Output(Bool())
  val flagOverflow = Output(Bool())
  val flagSign = Output(Bool())
  val flagZero = Output(Bool())
}

class ALUPort(implicit val conf:CAHPConfig) extends Bundle{
  val in = Input(new ALUPortIn())
  val out = new ALUPortOut()
}

class ALU(implicit val conf:CAHPConfig) extends Module {

  val io = IO(new ALUPort)
  val resCarry = Wire(UInt(17.W))
  resCarry := DontCare

  when(io.in.opcode === ALUOpcode.ADD) {
    io.out.out := io.in.inA + io.in.inB
  }.elsewhen(io.in.opcode === ALUOpcode.SUB) {
    // Keep the carry from A + ~B + 1.  Negating B at 16 bits first loses
    // that carry when B is zero and makes unsigned comparisons incorrect.
    resCarry := Cat(0.U(1.W), io.in.inA) +
      Cat(0.U(1.W), (~io.in.inB).asUInt) + 1.U
    io.out.out := resCarry(15, 0)
  }.elsewhen(io.in.opcode === ALUOpcode.AND) {
    io.out.out := io.in.inA & io.in.inB
  }.elsewhen(io.in.opcode === ALUOpcode.OR) {
    io.out.out := io.in.inA | io.in.inB
  }.elsewhen(io.in.opcode === ALUOpcode.XOR) {
    io.out.out := io.in.inA ^ io.in.inB
  }.elsewhen(io.in.opcode === ALUOpcode.LSL) {
    io.out.out := (io.in.inA << io.in.inB).asUInt
  }.elsewhen(io.in.opcode === ALUOpcode.LSR) {
    io.out.out := (io.in.inA >> io.in.inB).asUInt
  }.elsewhen(io.in.opcode === ALUOpcode.ASR) {
    io.out.out := (io.in.inA.asSInt >> io.in.inB).asUInt
  }.elsewhen(io.in.opcode === ALUOpcode.MOV) {
    io.out.out := io.in.inB
  }.otherwise {
    io.out.out := DontCare
  }

  io.out.flagCarry := ~resCarry(16)
  io.out.flagSign := io.out.out(15)
  io.out.flagZero := (io.out.out === 0.U(16.W))
  io.out.flagOverflow := (io.in.inA(15) =/= io.in.inB(15)) &&
    (io.out.out(15) =/= io.in.inA(15))
}
