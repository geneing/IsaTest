import isabelle.Isabelle_Thread
import isabelle.Server_Commands.Session_Build
import isabelle._
import scala.annotation.tailrec


object Main {

  def normalize(text: String): String =
    if (text.contains('\r')) text.replace("\r\n", "\n") else text

  private val all_messages = 
    Session.Consumer[Prover.Message](getClass.getName) {
      case output => 
        //println(s"all> ${output.toString()} ")
    }

  private val syslog_messages =
    Session.Consumer[Prover.Output](getClass.getName) { case output =>
      println(s"syslog> ${output.message} ")
    }

  private val raw_edits =
    Session.Consumer[Session.Raw_Edits](getClass.getName) { case output =>
      println(s"raw_edits> ${output.edits} ")
    }

  private val prover_output =
    Session.Consumer[Session.Commands_Changed](getClass.getName) {
      case changed =>
        println(s"prover> ${changed.toString()} ") //.   .nodes.toList.map( f=> f.node)} ")
    }

  private val raw_output_messages =
    Session.Consumer[Prover.Output](getClass.getName) {
      case output =>
        println(s"prover.output> ${output.toString()} ") //.   .nodes.toList.map( f=> f.node)} ")
    }

  def main(args: Array[String]): Unit = {

    val thread =
      Isabelle_Thread.fork(name = "blah", inherit_locals = true) {
        val rc =
          try {
            val id = UUID.random()
            Isabelle_System.init(isabelle_root =
              "/home/eingerman/Projects/DeepMath/Isabelle2021/"
            )
            val args1 = Session_Build.Args("HOL")
            val progress = new File_Progress(
              path = isabelle.Path.explode("IsaTest.log"),
              verbose = true
            )
            val logger =
              Logger.make(Option(isabelle.Path.explode("IsaTestLogger.log")))

            val (_, _, options, base_info) =
              try { Session_Build.command(args1, progress = progress) }
              catch {
                case exn: Server.Error => error(exn.message)
              }

            val resources = Headless.Resources(options, base_info, log = logger)
            val session = resources.start_session(
              print_mode = Nil,
              progress = progress
            )
            session.verbose = true
            session.all_messages += all_messages
            session.syslog_messages += syslog_messages
            //session.commands_changed += prover_output
            session.raw_edits += raw_edits
            session.raw_output_messages += raw_output_messages
            // val theories = List("First_Order_Logic")
            // val qualifier = Sessions.DRAFT
            // val master_dir = "/home/eingerman/Projects/DeepMath/Isabelle2021/src/Pure/Examples/"
            // val import_names = theories.map{thy => resources.import_name(qualifier, master_dir, thy) -> Position.none}
            // val dependencies = resources.dependencies(import_names, progress = progress).check_errors
            
            // val dep_theories = dependencies.theories
            // val dep_theories_set = dep_theories.toSet
            // val dep_files =
            //   for (path <- dependencies.loaded_files)
            //     yield Document.Node.Name(resources.append("", path))

            // println(s"depe> $dependencies ")
            // val thy_name = session.resources.make_theory_name(Document.Node.Name(node="First_Order_Logic",master_dir="/home/eingerman/Projects/DeepMath/Isabelle2021/src/Pure/Examples/"))
            // val thy_rr = session.resources.make_theory_content( thy_name.getOrElse(Document.Node.Name.empty) )

            @tailrec def get_snapshot_stable(node_name: Document.Node.Name, i: Int = 0): (Document.Snapshot, Document.Node) = {
              val snapshot = session.snapshot(node_name)

              if( snapshot.is_outdated ){
                Thread.sleep(400)
                get_snapshot_stable(node_name, i+1)
              }
              else {
                val node = snapshot.get_node(node_name)
                //header = node3.header
                // println(snapshot)
                // println(node.commands)
                println(i)
                (snapshot, node) 
              }
            }

            val thy_name = "Drinker"
            val dirname = "/home/eingerman/Projects/DeepMath/"

            val node_name = Document.Node.Name(resources.append(source_path=Path.explode(thy_name).thy,dir=dirname),theory="Drinker")
            val node_perspective: Document.Node.Perspective_Text =
                            Document.Node.Perspective(true, Text.Perspective.full, Document.Node.Overlays.empty)
            
            val bytes = Bytes.read(isabelle.Path.explode("/home/eingerman/Projects/DeepMath/Drinker.thy"))
            //val node_header = resources.check_thy(node_name, Scan.char_reader(bytes.text))

            val lines = bytes.text
            val line_list = lines.split("(\r\n)|\r|\n").filter(_.nonEmpty).toList
            var proof_text = ""
            
            // val new_blob = Document.Blob(bytes, lines, Symbol.Text_Chunk(lines), changed = true)

            // val edits: List[Document.Edit_Text] = List(
            //   node_name -> Document.Node.Edits(Text.Edit.inserts(0, bytes.text)),
            //   node_name->node_perspective
            // )
            // val doc_blobs = Document.Blobs{Map( node_name -> new_blob )}
            
            // session.update(doc_blobs, edits)

            // val (snapshot2, node) = get_snapshot_stable(node_name)

            // for( command <- snapshot2.node.commands.iterator ){
            //   println(s"${command.source} : ${snapshot2.command_results(command)} ")
            // }

            var header =  Document.Node.Header(imports_pos = List((Document.Node.Name("Main"),List(("offset","49"),("end_offset","53")))),
                        abbrevs=List(), errors=List(), keywords = List())



            // def get_offset( node_name: Document.Node.Name): Int = {
            //   val node = get_snapshot_stable(node_name)
            //   var cmd_offset =  (for( (cmd, offset) <- Document.Node.Commands.starts( node.commands.iterator)) yield offset).sum
            //   println("cmd_offset: ", cmd_offset)
            //   cmd_offset
            // }
            // for( proof_line <- line_list.iterator.take(12) ){
            //   //println(proof_line)
            //   var old_proof_text = normalize(proof_text)
            //   val node = get_snapshot_stable(node_name)
              
            //   val offset = get_offset(node_name) //(old_proof_text.length()-1).max(0)

            //   proof_text = proof_text + "\n" + proof_line 
            //   val bytes = Bytes(proof_text)
            //   val new_blob = Document.Blob(bytes, proof_text, Symbol.Text_Chunk(proof_text), changed = true)
            //   val edits: List[Document.Edit_Text] = List(node_name ->Document.Node.Deps(header), node_name -> Document.Node.Edits(Text.Edit.inserts(offset, proof_line)))
            //   val doc_blobs = Document.Blobs{Map( node_name -> new_blob )}
              
            //   session.update(doc_blobs, edits)
            // }

            // def node_perspective: Document.Node.Perspective_Text =
            //         Document.Node.Perspective(node_required, Text.Perspective.empty, Document.Node.Overlays.empty)

            for( proof_line <- line_list.iterator.take(12) ){
              //println(proof_line)
              var old_proof_text = proof_text
              val bytes = Bytes(old_proof_text)
              val old_blob = Document.Blob(bytes, old_proof_text, Symbol.Text_Chunk(old_proof_text), changed = true)

              proof_text = proof_text + "\n" + proof_line
              val new_blob = Document.Blob(bytes, proof_text, Symbol.Text_Chunk(proof_text), changed = true)
              val edits: List[Document.Edit_Text] = List(
                node_name ->Document.Node.Deps(header),
                node_name -> Document.Node.Edits(Text.Edit.replace(0, old_proof_text, proof_text)),
                node_name->node_perspective
              )

              val doc_blobs = Document.Blobs{Map( node_name -> new_blob )}
              
              session.update(doc_blobs, edits)
              
              // val state = session.get_state()
              // println(s"state:  $state")

              // val (snapshot, node) = get_snapshot_stable(node_name)
              // for( command <- snapshot.node.commands.iterator ){
              //   println(s"${command.source} : ${snapshot.command_results(command)} ")
              //   //println(snapshot.command_results(command))
              // }
            }

            val (snapshot, node) = get_snapshot_stable(node_name)
            val command1 = snapshot.node.commands.last 
            val results = snapshot.command_results(command1)
            //println(s"${command1.source} : ${results} ")
            println(s"${results}")



            // for( proof_line <- line_list.iterator.take(12) ){
            //   //println(proof_line)
            //   var old_proof_text = normalize(proof_text)
            //   val offset = old_proof_text.length()
            //   proof_text = proof_text + "\n" + proof_line 
            //   val bytes = Bytes(proof_text)
            //   val new_blob = Document.Blob(bytes, proof_text, Symbol.Text_Chunk(proof_text), changed = true)
            //   val edits: List[Document.Edit_Text] = List{node_name -> Document.Node.Edits(Text.Edit.inserts(offset, proof_line))}
            //   val doc_blobs = Document.Blobs{Map( node_name -> new_blob )}
              
            //   session.update(doc_blobs, edits)
            // val snapshot3 = session.snapshot(node_name)
            // println( snapshot3 )
            // val node3 = snapshot3.get_node(node_name)
            // println(node3.commands)

            // }

            // val command = session.snapshot(node_name).node.command_iterator(0)
            // for( c <- command ){
            //   println(c )
            // }

            

            // println(s"sess> $session ")
            // println(s"snap> $snapshot ")
            // println(s"state> $state ")
            // println(s"version> $version ")

            // println(s">>>>\n ${resources.html_document(snapshot)} ")

            0
          } catch {
            case exn: Throwable =>
              Output.error_message(
                Exn.message(exn) + (if (true) "\n" + Exn.trace(exn) else "")
              )
              Exn.return_code(exn, 2)
          }
        sys.exit(rc)
      }
    thread.join

  }
}


// declare -v CLASSPATH=/home/eingerman/Projects/DeepMath/isabelle/lib/classes/Pure.jar:/home/eingerman/.isabelle/contrib/flatlaf-1.0/lib/flatlaf-1.0.jar:/home/eingerman/.isabelle/contrib/jfreechart-1.5.1/lib/iText-2.1.5.jar:/home/eingerman/.isabelle/contrib/jfreechart-1.5.1/lib/jfreechart-1.5.1.jar:/home/eingerman/.isabelle/contrib/jortho-1.0-2/jortho.jar:/home/eingerman/.isabelle/contrib/kodkodi-1.5.6/jar/antlr-runtime-3.1.1.jar:/home/eingerman/.isabelle/contrib/kodkodi-1.5.6/jar/kodkod-1.5.jar:/home/eingerman/.isabelle/contrib/kodkodi-1.5.6/jar/kodkodi-1.5.6.jar:/home/eingerman/.isabelle/contrib/kodkodi-1.5.6/jar/sat4j-2.3.jar:/home/eingerman/.isabelle/contrib/postgresql-42.2.18/postgresql-42.2.18.jar:/home/eingerman/.isabelle/contrib/scala-2.13.4-1/lib/jline-3.16.0.jar:/home/eingerman/.isabelle/contrib/scala-2.13.4-1/lib/jna-5.3.1.jar:/home/eingerman/.isabelle/contrib/scala-2.13.4-1/lib/scala-compiler.jar:/home/eingerman/.isabelle/contrib/scala-2.13.4-1/lib/scala-library.jar:/home/eingerman/.isabelle/contrib/scala-2.13.4-1/lib/scala-parallel-collections_2.13-1.0.0.jar:/home/eingerman/.isabelle/contrib/scala-2.13.4-1/lib/scala-parser-combinators_2.13-1.1.2.jar:/home/eingerman/.isabelle/contrib/scala-2.13.4-1/lib/scala-reflect.jar:/home/eingerman/.isabelle/contrib/scala-2.13.4-1/lib/scala-swing_2.13-2.1.1.jar:/home/eingerman/.isabelle/contrib/scala-2.13.4-1/lib/scala-xml_2.13-1.3.0.jar:/home/eingerman/.isabelle/contrib/scala-2.13.4-1/lib/scalap-2.13.4.jar:/home/eingerman/.isabelle/contrib/sqlite-jdbc-3.34.0/sqlite-jdbc-3.34.0.jar:/home/eingerman/.isabelle/contrib/ssh-java-20190323/lib/jsch-0.1.55.jar:/home/eingerman/.isabelle/contrib/ssh-java-20190323/lib/jzlib-1.1.3.jar:/home/eingerman/.isabelle/contrib/ssh-java-20190323/lib/jce.jar:/home/eingerman/.isabelle/contrib/xz-java-1.8/lib/xz.jar
// /home/eingerman/.isabelle/contrib/scala-2.13.4-1/bin/scalac -encoding UTF-8 -nowarn -target:jvm-1.8 -J-Xms512m -J-Xmx4g -J-Xss16m -g:source -g:vars -d lib/classes/build src/HOL/SPARK/Tools/spark.scala src/HOL/Tools/Nitpick/kodkod.scala src/Pure/Admin/afp.scala src/Pure/Admin/build_csdp.scala src/Pure/Admin/build_cygwin.scala src/Pure/Admin/build_doc.scala src/Pure/Admin/build_e.scala src/Pure/Admin/build_fonts.scala src/Pure/Admin/build_history.scala src/Pure/Admin/build_jdk.scala src/Pure/Admin/build_log.scala src/Pure/Admin/build_polyml.scala src/Pure/Admin/build_release.scala src/Pure/Admin/build_spass.scala src/Pure/Admin/build_sqlite.scala src/Pure/Admin/build_status.scala src/Pure/Admin/build_vampire.scala src/Pure/Admin/build_verit.scala src/Pure/Admin/build_zipperposition.scala src/Pure/Admin/check_sources.scala src/Pure/Admin/ci_profile.scala src/Pure/Admin/components.scala src/Pure/Admin/isabelle_cronjob.scala src/Pure/Admin/isabelle_devel.scala src/Pure/Admin/jenkins.scala src/Pure/Admin/other_isabelle.scala src/Pure/Concurrent/consumer_thread.scala src/Pure/Concurrent/counter.scala src/Pure/Concurrent/delay.scala src/Pure/Concurrent/event_timer.scala src/Pure/Concurrent/future.scala src/Pure/Concurrent/isabelle_thread.scala src/Pure/Concurrent/mailbox.scala src/Pure/Concurrent/par_list.scala src/Pure/Concurrent/synchronized.scala src/Pure/GUI/color_value.scala src/Pure/GUI/desktop_app.scala src/Pure/GUI/gui.scala src/Pure/GUI/gui_thread.scala src/Pure/GUI/popup.scala src/Pure/GUI/wrap_panel.scala src/Pure/General/antiquote.scala src/Pure/General/bytes.scala src/Pure/General/cache.scala src/Pure/General/codepoint.scala src/Pure/General/comment.scala src/Pure/General/completion.scala src/Pure/General/csv.scala src/Pure/General/date.scala src/Pure/General/exn.scala src/Pure/General/file.scala src/Pure/General/file_watcher.scala src/Pure/General/graph.scala src/Pure/General/graph_display.scala src/Pure/General/graphics_file.scala src/Pure/General/http.scala src/Pure/General/json.scala src/Pure/General/linear_set.scala src/Pure/General/logger.scala src/Pure/General/long_name.scala src/Pure/General/mailman.scala src/Pure/General/mercurial.scala src/Pure/General/multi_map.scala src/Pure/General/output.scala src/Pure/General/path.scala src/Pure/General/position.scala src/Pure/General/pretty.scala src/Pure/General/properties.scala src/Pure/General/rdf.scala src/Pure/General/scan.scala src/Pure/General/sha1.scala src/Pure/General/sql.scala src/Pure/General/ssh.scala src/Pure/General/symbol.scala src/Pure/General/time.scala src/Pure/General/timing.scala src/Pure/General/untyped.scala src/Pure/General/url.scala src/Pure/General/utf8.scala src/Pure/General/uuid.scala src/Pure/General/value.scala src/Pure/General/word.scala src/Pure/General/xz.scala src/Pure/Isar/document_structure.scala src/Pure/Isar/keyword.scala src/Pure/Isar/line_structure.scala src/Pure/Isar/outer_syntax.scala src/Pure/Isar/parse.scala src/Pure/Isar/token.scala src/Pure/ML/ml_console.scala src/Pure/ML/ml_lex.scala src/Pure/ML/ml_process.scala src/Pure/ML/ml_statistics.scala src/Pure/ML/ml_syntax.scala src/Pure/PIDE/byte_message.scala src/Pure/PIDE/command.scala src/Pure/PIDE/command_span.scala src/Pure/PIDE/document.scala src/Pure/PIDE/document_id.scala src/Pure/PIDE/document_status.scala src/Pure/PIDE/editor.scala src/Pure/PIDE/headless.scala src/Pure/PIDE/line.scala src/Pure/PIDE/markup.scala src/Pure/PIDE/markup_tree.scala src/Pure/PIDE/protocol.scala src/Pure/PIDE/protocol_handlers.scala src/Pure/PIDE/protocol_message.scala src/Pure/PIDE/prover.scala src/Pure/PIDE/query_operation.scala src/Pure/PIDE/rendering.scala src/Pure/PIDE/resources.scala src/Pure/PIDE/session.scala src/Pure/PIDE/text.scala src/Pure/PIDE/xml.scala src/Pure/PIDE/yxml.scala src/Pure/ROOT.scala src/Pure/System/bash.scala src/Pure/System/command_line.scala src/Pure/System/cygwin.scala src/Pure/System/distribution.scala src/Pure/System/executable.scala src/Pure/System/getopts.scala src/Pure/System/isabelle_charset.scala src/Pure/System/isabelle_fonts.scala src/Pure/System/isabelle_platform.scala src/Pure/System/isabelle_process.scala src/Pure/System/isabelle_system.scala src/Pure/System/isabelle_tool.scala src/Pure/System/java_statistics.scala src/Pure/System/linux.scala src/Pure/System/mingw.scala src/Pure/System/numa.scala src/Pure/System/options.scala src/Pure/System/platform.scala src/Pure/System/posix_interrupt.scala src/Pure/System/process_result.scala src/Pure/System/progress.scala src/Pure/System/scala.scala src/Pure/System/system_channel.scala src/Pure/System/tty_loop.scala src/Pure/Thy/bibtex.scala src/Pure/Thy/export.scala src/Pure/Thy/export_theory.scala src/Pure/Thy/file_format.scala src/Pure/Thy/html.scala src/Pure/Thy/latex.scala src/Pure/Thy/presentation.scala src/Pure/Thy/sessions.scala src/Pure/Thy/thy_element.scala src/Pure/Thy/thy_header.scala src/Pure/Thy/thy_syntax.scala src/Pure/Tools/build.scala src/Pure/Tools/build_docker.scala src/Pure/Tools/build_job.scala src/Pure/Tools/check_keywords.scala src/Pure/Tools/debugger.scala src/Pure/Tools/doc.scala src/Pure/Tools/dump.scala src/Pure/Tools/fontforge.scala src/Pure/Tools/java_monitor.scala src/Pure/Tools/main.scala src/Pure/Tools/mkroot.scala src/Pure/Tools/phabricator.scala src/Pure/Tools/print_operation.scala src/Pure/Tools/profiling_report.scala src/Pure/Tools/scala_project.scala src/Pure/Tools/server.scala src/Pure/Tools/server_commands.scala src/Pure/Tools/simplifier_trace.scala src/Pure/Tools/spell_checker.scala src/Pure/Tools/task_statistics.scala src/Pure/Tools/update.scala src/Pure/Tools/update_cartouches.scala src/Pure/Tools/update_comments.scala src/Pure/Tools/update_header.scala src/Pure/Tools/update_then.scala src/Pure/Tools/update_theorems.scala src/Pure/library.scala src/Pure/pure_thy.scala src/Pure/term.scala src/Pure/term_xml.scala src/Pure/thm_name.scala src/Tools/Graphview/graph_file.scala src/Tools/Graphview/graph_panel.scala src/Tools/Graphview/graphview.scala src/Tools/Graphview/layout.scala src/Tools/Graphview/main_panel.scala src/Tools/Graphview/metrics.scala src/Tools/Graphview/model.scala src/Tools/Graphview/mutator.scala src/Tools/Graphview/mutator_dialog.scala src/Tools/Graphview/mutator_event.scala src/Tools/Graphview/popups.scala src/Tools/Graphview/shapes.scala src/Tools/Graphview/tree_panel.scala src/Tools/VSCode/src/build_vscode.scala src/Tools/VSCode/src/channel.scala src/Tools/VSCode/src/dynamic_output.scala src/Tools/VSCode/src/language_server.scala src/Tools/VSCode/src/lsp.scala src/Tools/VSCode/src/preview_panel.scala src/Tools/VSCode/src/state_panel.scala src/Tools/VSCode/src/textmate_grammar.scala src/Tools/VSCode/src/vscode_model.scala src/Tools/VSCode/src/vscode_rendering.scala src/Tools/VSCode/src/vscode_resources.scala src/Tools/VSCode/src/vscode_spell_checker.scala -verbose
// /home/eingerman/.isabelle/contrib/jdk-15.0.2+7/x86_64-linux/bin/jar -c -f lib/classes/Pure.jar -e isabelle.Main -C lib/classes/ META-INF -C lib/classes/ isabelle
// shasum -a1 -b lib/classes/Pure.jar src/HOL/SPARK/Tools/spark.scala src/HOL/Tools/Nitpick/kodkod.scala src/Pure/Admin/afp.scala src/Pure/Admin/build_csdp.scala src/Pure/Admin/build_cygwin.scala src/Pure/Admin/build_doc.scala src/Pure/Admin/build_e.scala src/Pure/Admin/build_fonts.scala src/Pure/Admin/build_history.scala src/Pure/Admin/build_jdk.scala src/Pure/Admin/build_log.scala src/Pure/Admin/build_polyml.scala src/Pure/Admin/build_release.scala src/Pure/Admin/build_spass.scala src/Pure/Admin/build_sqlite.scala src/Pure/Admin/build_status.scala src/Pure/Admin/build_vampire.scala src/Pure/Admin/build_verit.scala src/Pure/Admin/build_zipperposition.scala src/Pure/Admin/check_sources.scala src/Pure/Admin/ci_profile.scala src/Pure/Admin/components.scala src/Pure/Admin/isabelle_cronjob.scala src/Pure/Admin/isabelle_devel.scala src/Pure/Admin/jenkins.scala src/Pure/Admin/other_isabelle.scala src/Pure/Concurrent/consumer_thread.scala src/Pure/Concurrent/counter.scala src/Pure/Concurrent/delay.scala src/Pure/Concurrent/event_timer.scala src/Pure/Concurrent/future.scala src/Pure/Concurrent/isabelle_thread.scala src/Pure/Concurrent/mailbox.scala src/Pure/Concurrent/par_list.scala src/Pure/Concurrent/synchronized.scala src/Pure/GUI/color_value.scala src/Pure/GUI/desktop_app.scala src/Pure/GUI/gui.scala src/Pure/GUI/gui_thread.scala src/Pure/GUI/popup.scala src/Pure/GUI/wrap_panel.scala src/Pure/General/antiquote.scala src/Pure/General/bytes.scala src/Pure/General/cache.scala src/Pure/General/codepoint.scala src/Pure/General/comment.scala src/Pure/General/completion.scala src/Pure/General/csv.scala src/Pure/General/date.scala src/Pure/General/exn.scala src/Pure/General/file.scala src/Pure/General/file_watcher.scala src/Pure/General/graph.scala src/Pure/General/graph_display.scala src/Pure/General/graphics_file.scala src/Pure/General/http.scala src/Pure/General/json.scala src/Pure/General/linear_set.scala src/Pure/General/logger.scala src/Pure/General/long_name.scala src/Pure/General/mailman.scala src/Pure/General/mercurial.scala src/Pure/General/multi_map.scala src/Pure/General/output.scala src/Pure/General/path.scala src/Pure/General/position.scala src/Pure/General/pretty.scala src/Pure/General/properties.scala src/Pure/General/rdf.scala src/Pure/General/scan.scala src/Pure/General/sha1.scala src/Pure/General/sql.scala src/Pure/General/ssh.scala src/Pure/General/symbol.scala src/Pure/General/time.scala src/Pure/General/timing.scala src/Pure/General/untyped.scala src/Pure/General/url.scala src/Pure/General/utf8.scala src/Pure/General/uuid.scala src/Pure/General/value.scala src/Pure/General/word.scala src/Pure/General/xz.scala src/Pure/Isar/document_structure.scala src/Pure/Isar/keyword.scala src/Pure/Isar/line_structure.scala src/Pure/Isar/outer_syntax.scala src/Pure/Isar/parse.scala src/Pure/Isar/token.scala src/Pure/ML/ml_console.scala src/Pure/ML/ml_lex.scala src/Pure/ML/ml_process.scala src/Pure/ML/ml_statistics.scala src/Pure/ML/ml_syntax.scala src/Pure/PIDE/byte_message.scala src/Pure/PIDE/command.scala src/Pure/PIDE/command_span.scala src/Pure/PIDE/document.scala src/Pure/PIDE/document_id.scala src/Pure/PIDE/document_status.scala src/Pure/PIDE/editor.scala src/Pure/PIDE/headless.scala src/Pure/PIDE/line.scala src/Pure/PIDE/markup.scala src/Pure/PIDE/markup_tree.scala src/Pure/PIDE/protocol.scala src/Pure/PIDE/protocol_handlers.scala src/Pure/PIDE/protocol_message.scala src/Pure/PIDE/prover.scala src/Pure/PIDE/query_operation.scala src/Pure/PIDE/rendering.scala src/Pure/PIDE/resources.scala src/Pure/PIDE/session.scala src/Pure/PIDE/text.scala src/Pure/PIDE/xml.scala src/Pure/PIDE/yxml.scala src/Pure/ROOT.scala src/Pure/System/bash.scala src/Pure/System/command_line.scala src/Pure/System/cygwin.scala src/Pure/System/distribution.scala src/Pure/System/executable.scala src/Pure/System/getopts.scala src/Pure/System/isabelle_charset.scala src/Pure/System/isabelle_fonts.scala src/Pure/System/isabelle_platform.scala src/Pure/System/isabelle_process.scala src/Pure/System/isabelle_system.scala src/Pure/System/isabelle_tool.scala src/Pure/System/java_statistics.scala src/Pure/System/linux.scala src/Pure/System/mingw.scala src/Pure/System/numa.scala src/Pure/System/options.scala src/Pure/System/platform.scala src/Pure/System/posix_interrupt.scala src/Pure/System/process_result.scala src/Pure/System/progress.scala src/Pure/System/scala.scala src/Pure/System/system_channel.scala src/Pure/System/tty_loop.scala src/Pure/Thy/bibtex.scala src/Pure/Thy/export.scala src/Pure/Thy/export_theory.scala src/Pure/Thy/file_format.scala src/Pure/Thy/html.scala src/Pure/Thy/latex.scala src/Pure/Thy/presentation.scala src/Pure/Thy/sessions.scala src/Pure/Thy/thy_element.scala src/Pure/Thy/thy_header.scala src/Pure/Thy/thy_syntax.scala src/Pure/Tools/build.scala src/Pure/Tools/build_docker.scala src/Pure/Tools/build_job.scala src/Pure/Tools/check_keywords.scala src/Pure/Tools/debugger.scala src/Pure/Tools/doc.scala src/Pure/Tools/dump.scala src/Pure/Tools/fontforge.scala src/Pure/Tools/java_monitor.scala src/Pure/Tools/main.scala src/Pure/Tools/mkroot.scala src/Pure/Tools/phabricator.scala src/Pure/Tools/print_operation.scala src/Pure/Tools/profiling_report.scala src/Pure/Tools/scala_project.scala src/Pure/Tools/server.scala src/Pure/Tools/server_commands.scala src/Pure/Tools/simplifier_trace.scala src/Pure/Tools/spell_checker.scala src/Pure/Tools/task_statistics.scala src/Pure/Tools/update.scala src/Pure/Tools/update_cartouches.scala src/Pure/Tools/update_comments.scala src/Pure/Tools/update_header.scala src/Pure/Tools/update_then.scala src/Pure/Tools/update_theorems.scala src/Pure/library.scala src/Pure/pure_thy.scala src/Pure/term.scala src/Pure/term_xml.scala src/Pure/thm_name.scala src/Tools/Graphview/graph_file.scala src/Tools/Graphview/graph_panel.scala src/Tools/Graphview/graphview.scala src/Tools/Graphview/layout.scala src/Tools/Graphview/main_panel.scala src/Tools/Graphview/metrics.scala src/Tools/Graphview/model.scala src/Tools/Graphview/mutator.scala src/Tools/Graphview/mutator_dialog.scala src/Tools/Graphview/mutator_event.scala src/Tools/Graphview/popups.scala src/Tools/Graphview/shapes.scala src/Tools/Graphview/tree_panel.scala src/Tools/VSCode/src/build_vscode.scala src/Tools/VSCode/src/channel.scala src/Tools/VSCode/src/dynamic_output.scala src/Tools/VSCode/src/language_server.scala src/Tools/VSCode/src/lsp.scala src/Tools/VSCode/src/preview_panel.scala src/Tools/VSCode/src/state_panel.scala src/Tools/VSCode/src/textmate_grammar.scala src/Tools/VSCode/src/vscode_model.scala src/Tools/VSCode/src/vscode_rendering.scala src/Tools/VSCode/src/vscode_resources.scala src/Tools/VSCode/src/vscode_spell_checker.scala 2>/dev/null > lib/classes/Pure.shasum