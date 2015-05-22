object FileSystem {
  import java.io.File
  import scala.util.matching.Regex

  final def listFiles(base: File, recursive: Boolean = true): Seq[File] = {
    val files = base.listFiles
    val result = files.filter(_.isFile)
    result ++
      files
        .filter(_.isDirectory)
        .filter(_ => recursive)
        .flatMap(listFiles(_, recursive))
  }

  final def listClasses(base: File, regex: Regex, recursive: Boolean = true) : Seq[IndexedSeq[String]] =
    listFiles(base)
      .filter(f => regex.findFirstIn(f.getAbsolutePath).isDefined)
      .map(f => regex.findFirstMatchIn(f.getAbsolutePath))
      .flatten
      .map(m => m.subgroups.toIndexedSeq)
}
