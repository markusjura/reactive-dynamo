package reactivedynamo.clients.producer;

import akka.event.LoggingAdapter;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.ArrayList;

/**
 * Helper method is taken from:
 * http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/AppendixSampleDataCodeJava.html
 */


public class TableHelper {

    private LoggingAdapter log;

    public TableHelper(LoggingAdapter log) {
        this.log = log;
    }

    public void createTable(
            String tableName, long readCapacityUnits, long writeCapacityUnits,
            String partitionKeyName, String partitionKeyType , DynamoDB client) {

        createTable(tableName, readCapacityUnits, writeCapacityUnits,
                partitionKeyName, partitionKeyType, null, null,client);
    }

    public  void createTable(
            String tableName, long readCapacityUnits, long writeCapacityUnits,
            String partitionKeyName, String partitionKeyType,
            String sortKeyName, String sortKeyType,DynamoDB client) {


        try {

            ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            keySchema.add(new KeySchemaElement()
                    .withAttributeName(partitionKeyName)
                    .withKeyType(KeyType.HASH)); //Partition key

            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
            attributeDefinitions.add(new AttributeDefinition()
                    .withAttributeName(partitionKeyName)
                    .withAttributeType(partitionKeyType));

            if (sortKeyName != null) {
                keySchema.add(new KeySchemaElement()
                        .withAttributeName(sortKeyName)
                        .withKeyType(KeyType.RANGE)); //Sort key
                attributeDefinitions.add(new AttributeDefinition()
                        .withAttributeName(sortKeyName)
                        .withAttributeType(sortKeyType));
            }

            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withProvisionedThroughput( new ProvisionedThroughput()
                            .withReadCapacityUnits(readCapacityUnits)
                            .withWriteCapacityUnits(writeCapacityUnits));

            // If this is the Reply table, define a local secondary index
            if ("Reply".equals(tableName)) {

                attributeDefinitions.add(new AttributeDefinition()
                        .withAttributeName("PostedBy")
                        .withAttributeType("S"));

                ArrayList<LocalSecondaryIndex> localSecondaryIndexes = new ArrayList<LocalSecondaryIndex>();
                localSecondaryIndexes.add(new LocalSecondaryIndex()
                        .withIndexName("PostedBy-Index")
                        .withKeySchema(
                                new KeySchemaElement().withAttributeName(partitionKeyName).withKeyType(KeyType.HASH),  //Partition key
                                new KeySchemaElement() .withAttributeName("PostedBy") .withKeyType(KeyType.RANGE))  //Sort key
                        .withProjection(new Projection() .withProjectionType(ProjectionType.KEYS_ONLY)));

                request.setLocalSecondaryIndexes(localSecondaryIndexes);
            }

            request.setAttributeDefinitions(attributeDefinitions);

            log.info("Creating table {}", tableName);
            Table table = client.createTable(request);
            table.waitForActive();
            log.info("Table {} has been created.", tableName);

        } catch (Exception e) {
            log.error("CreateTable request failed for " + tableName);
            log.error(e.getMessage());
        }
    }

    public void deleteTable(String tableName, DynamoDB db) {
        Table table = db.getTable(tableName);
        try {
            log.info("Issuing DeleteTable request for " + tableName);
            table.delete();
            log.info("Waiting for " + tableName
                    + " to be deleted...this may take a while...");
            table.waitForDelete();

        } catch (Exception e) {
            log.error("DeleteTable request failed for " + tableName);
            log.error(e.getMessage());
        }
    }
}
