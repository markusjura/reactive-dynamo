package reactive.dynamo

import java.util.Date

import akka.util.ByteString

case class Record(awsRegion: String, eventId: String, eventName: EventName, payload: StreamRecord)

case class StreamRecord(creationDate: Date, sequenceNumber: String, streamViewType: StreamViewType, keys: Item,
  newImage: Option[Item], oldImage: Option[Item])

case class Item(attributes: Map[String, Attribute])

sealed trait Attribute

case class BinaryAttribute(value: ByteString) extends Attribute
case class BooleanAttribute(value: Boolean) extends Attribute
case class BinarySetAttribute(value: Seq[ByteString]) extends Attribute
case class ListAttribute(value: Seq[Attribute]) extends Attribute
case class MapAttribute(value: Item) extends Attribute
case class NumberAttribute(value: Number) extends Attribute
case class NumberSetAttribute(value: Seq[Number]) extends Attribute
case object NullAttribute extends Attribute
case class StringAttribute(value: String) extends Attribute
case class StringSetAttribute(value: Seq[String]) extends Attribute

sealed trait EventName

object EventName {
  case object Insert extends EventName
  case object Modify extends EventName
  case object Remove extends EventName
}

sealed trait StreamViewType

object StreamViewType {
  case object KeysOnly extends StreamViewType
  case object NewImage extends StreamViewType
  case object OldImage extends StreamViewType
  case object NewAndOldImages extends StreamViewType
}

