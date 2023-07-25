import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.cluster.typed.Cluster
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.javadsl.AkkaManagement
import akka.{actor => classic}
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.trace.{Span, SpanKind}

object Main extends App {

  ActorSystem[Nothing](Behaviors.setup[Nothing] { ctx =>
    import akka.actor.typed.scaladsl.adapter._
    implicit val classicSystem: classic.ActorSystem = ctx.system.toClassic

    val cluster = Cluster(ctx.system)
    ctx.log.info("Started [" + ctx.system + "], cluster.selfAddress = " + cluster.selfMember.address + ")")

    // Create an actor that handles cluster domain events
    //val listener = context.spawn(Behaviors.receive[ClusterEvent.MemberEvent]((ctx, event) => {
    //  ctx.log.info("MemberEvent: {}", event)
    //  Behaviors.same
    //}), "listener")

    //Cluster(context.system).subscriptions ! Subscribe(listener, classOf[ClusterEvent.MemberEvent])

    AkkaManagement.get(classicSystem).start()
    ClusterBootstrap.get(classicSystem).start()

    val ping = ctx.spawn(PingPong(), "ping")
    val pong = ctx.spawn(PingPong(), "pong")
    ctx.log.info("@@ ping actor-path: ", ping.path.toString)
    ctx.log.info("@@ pong actor-path: ", pong.path.toString)

    Http().newServerAt("0.0.0.0", 8080).bind(Routes(ping, pong))

    Behaviors.empty
  }, "appka")

}

object Routes {

  def apply(pingRef: ActorRef[PingPong.Cmd], pongRef: ActorRef[PingPong.Cmd]): akka.http.scaladsl.server.Route = {
    path("ping") {
      get {
        //val span = OpenTelemetry.tracer
        //  .spanBuilder("GET /ping")
        //  .setSpanKind(SpanKind.SERVER)
        //  .startSpan()
        //span.makeCurrent()
        Span.current().setAllAttributes(
          Attributes.builder()
            .put("ping_ref", pingRef.toString)
            .put("hostname", sys.env.getOrElse("HOSTNAME", "N/A"))
            .build()
        )

        pingRef ! PingPong.Ping("hoge", pongRef)

        //span.end()
        complete(StatusCodes.OK)
      }
    }
  }
}

