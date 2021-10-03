import isabelle.Isabelle_Thread
import isabelle.Server_Commands.Session_Build
import isabelle._

object Main  {


  private val syslog_messages = Session.Consumer[Prover.Output](getClass.getName) {
      case output => println(output.message)
    }

  def main(args: Array[String]): Unit = {

    val thread =
      Isabelle_Thread.fork(name = "blah", inherit_locals = true) {
        val rc =
          try {
            val id = UUID.random()
            Isabelle_System.init(isabelle_root = "/home/eingerman/Projects/DeepMath/Isabelle2021/")
            val args1 = Session_Build.Args("HOL")
            val progress = new File_Progress(path=isabelle.Path.explode("IsaTest.log"), verbose=true)
            val logger = Logger.make(Option(isabelle.Path.explode("IsaTestLogger.log")))

            val (_, _, options, base_info) =
              try { Session_Build.command(args1, progress = progress) }
              catch {
                case exn: Server.Error => error(exn.message)
              }

            val resources = Headless.Resources(options, base_info, log = logger)
            val session = resources.start_session(print_mode = List("brackets"), progress = progress)

            session.sys
            val res1 = session.use_theories(List("First_Order_Logic"), master_dir="/home/eingerman/Projects/DeepMath/Isabelle2021/src/Pure/Examples/", progress = progress)
            // println(res1.state)
            // println(res1.nodes)
            
            println(session)

            0
          }
          catch {
            case exn: Throwable =>
              Output.error_message(Exn.message(exn) + (if (true) "\n" + Exn.trace(exn) else ""))
              Exn.return_code(exn, 2)
          }
        sys.exit(rc)
      }
    thread.join

  }
}
