package controllers

import javax.inject.{ Inject, Named }

import akka.actor.{ ActorRef, ActorSystem }

/**
 * Created by fscoward on 2017/06/16.
 */
class MyScheduler @Inject() (system: ActorSystem, @Named("my-actor") actor: ActorRef) {
}
