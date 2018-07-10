import scala.reflect.runtime.universe

case class Case(foo: Int) {
  println("Case Case Instantiated")
}

class Class {
  println("Class Instantiated")
}

object Inst {

  def apply(className: String, arg: Any) = {
    val runtimeMirror: universe.Mirror = universe.runtimeMirror(getClass.getClassLoader)

    val classSymbol: universe.ClassSymbol = runtimeMirror.classSymbol(Class.forName(className))

    val classMirror: universe.ClassMirror = runtimeMirror.reflectClass(classSymbol)

    if (classSymbol.companion.toString() == "<none>") // TODO: use nicer method "hiding" in the api?
    {
      println(s"Info: $className has no companion object")
      val constructors = classSymbol.typeSignature.members.filter(_.isConstructor).toList
      if (constructors.length > 1) {
        println(s"Info: $className has several constructors")
      }
      else {
        val constructorMirror = classMirror.reflectConstructor(constructors.head.asMethod) // we can reuse it
        constructorMirror()
      }

    }
    else
    {
      val companionSymbol = classSymbol.companion
      println(s"Info: $className has companion object $companionSymbol")
      // TBD
    }

  }
}

object app extends App {
  val c = Inst("Class", "")
  val cc = Inst("Case", "")
}