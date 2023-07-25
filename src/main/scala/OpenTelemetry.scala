import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.{Span, SpanKind, Tracer}

object OpenTelemetry {

  val tracer: Tracer = GlobalOpenTelemetry.tracerBuilder("test-trace").build()

  def createInternalSpan(name: String): Span = {
    val span = tracer
    .spanBuilder(name)
    .setSpanKind(SpanKind.INTERNAL)
    .startSpan()
    span.makeCurrent()
    span
  }

}
