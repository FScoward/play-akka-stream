package modules

import actors.MySupervisorActor
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport


class MyModule extends AbstractModule with AkkaGuiceSupport {
  def configure = {
    bindActor[MySupervisorActor]("my-actor")
  }
}
