package app

import com.amazonaws.services.dynamodbv2.document.Item

import scala.util.Random

object ItemGenerator {

  private final val Authors = List(
    "Michael Jackson",
    "Homer Simpson",
    "Hillary Clinton",
    "Donald Trump",
    "Barrack Obama")

  def next: Item = {
    new Item()
      .withPrimaryKey("Id", Math.abs(Random.nextInt()))
      .withString("Title", "Book Title")
      .withString("ISBN", s"120-${Random.nextInt()}")
      .withString("Author", Authors(Random.nextInt(Authors.size)))
      .withNumber("Price", 19.99)
      .withString("ProductCategory", "Book")
  }
}
