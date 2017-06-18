package service

import javax.inject.Inject

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.s3.scaladsl.S3Client
import akka.stream.scaladsl.{ Sink, Source }
import akka.util.ByteString

import scala.concurrent.Future

/**
 * Created by fscoward on 2017/06/18.
 */
class S3Service @Inject() (implicit system: ActorSystem) {
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val s3Client = S3Client()

  //  val s3Source: Source[ByteString, NotUsed] = s3Client.download("bucket", "bucketKey")

  //  val contents: Future[String] = s3Client.download("bucket", "key").runWith(Sink.reduce[ByteString](_ ++ _)).map(_.utf8String)

}
