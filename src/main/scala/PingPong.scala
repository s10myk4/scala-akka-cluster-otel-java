import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.trace.Span

object PingPong {
  sealed trait Cmd
  final case class Ping(msg: String, actorRef: ActorRef[Cmd]) extends Cmd
  final case class Pong(msg: String) extends Cmd

  def apply(): Behavior[Cmd] = Behaviors.setup { ctx =>
    Behaviors.receiveMessage {
      case Ping(msg, actorRef) =>
        val span = OpenTelemetry.createInternalSpan("receive_message")
        span.setAllAttributes(
          Attributes.builder()
            .put("command.actor_ref", actorRef.toString)
            .put("command.name", "Ping")
            .put("command.msg", msg)
            .put("hostname", sys.env.getOrElse("HOSTNAME", "N/A"))
            .build()
        )

        Thread.sleep(50)
        actorRef ! Pong(msg)
        ctx.log.info("Span.current: {}", Span.current())
        span.end()
        Behaviors.same
      case Pong(msg) =>
        val span = OpenTelemetry.createInternalSpan("receive_message")
        span.setAllAttributes(
          Attributes.builder()
            .put("command.name", "Pong")
            .put("command.msg", msg)
            .build()
        )
        Thread.sleep(70)
        ctx.log.info("received msg: {}", msg)
        ctx.log.info("Span.current: {}", Span.current())
        span.end()
        Behaviors.same
    }
  }

}
