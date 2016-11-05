// Copyright 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache License, Version 2.0.
package reactive.dynamo;

import java.util.List;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBStreamsClient;
import com.amazonaws.services.dynamodbv2.model.DescribeStreamRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeStreamResult;
import com.amazonaws.services.dynamodbv2.model.GetRecordsRequest;
import com.amazonaws.services.dynamodbv2.model.GetRecordsResult;
import com.amazonaws.services.dynamodbv2.model.GetShardIteratorRequest;
import com.amazonaws.services.dynamodbv2.model.GetShardIteratorResult;
import com.amazonaws.services.dynamodbv2.model.Record;
import com.amazonaws.services.dynamodbv2.model.Shard;
import com.amazonaws.services.dynamodbv2.model.ShardIteratorType;

public class StreamClient {

	private static AmazonDynamoDBStreamsClient streamsClient =
			new AmazonDynamoDBStreamsClient(new ProfileCredentialsProvider());

	public static void main(String args[]) throws InterruptedException {

		streamsClient.setEndpoint("http://localhost:8000");
//		streamsClient.setEndpoint("streams.dynamodb.eu-west-1.amazonaws.com");

		String myStreamArn = "arn:aws:dynamodb:ddblocal:000000000000:table/TestTableForStreams/stream/2016-11-05T12:23:25.246";

		// Get the shards in the stream

		DescribeStreamResult describeStreamResult =
				streamsClient.describeStream(new DescribeStreamRequest()
						.withStreamArn(myStreamArn));
		String streamArn =
				describeStreamResult.getStreamDescription().getStreamArn();
		List<Shard> shards =
				describeStreamResult.getStreamDescription().getShards();
		System.out.println("found shards " + shards.size());
		// Process each shard
		int numChanges =  2;
		for (Shard shard : shards) {
			String shardId = shard.getShardId();
			System.out.println(
					"Processing " + shardId + " from stream "+ streamArn + " with parent " + shard.getParentShardId());

			// Get an iterator for the current shard

			GetShardIteratorRequest getShardIteratorRequest = new GetShardIteratorRequest()
					.withStreamArn(myStreamArn)
					.withShardId(shardId)
					.withShardIteratorType(ShardIteratorType.TRIM_HORIZON);
			GetShardIteratorResult getShardIteratorResult =
					streamsClient.getShardIterator(getShardIteratorRequest);
			String nextItr = getShardIteratorResult.getShardIterator();


			while (nextItr != null) {
//			while (nextItr != null && numChanges > 0) {

				// Use the iterator to read the data records from the shard

				GetRecordsResult getRecordsResult =
						streamsClient.getRecords(new GetRecordsRequest().
								withShardIterator(nextItr));
				List<Record> records = getRecordsResult.getRecords();
				System.out.println("Getting records...");
				if (records.isEmpty()) {
					Thread.sleep(2000);
				}
				for (Record record : records) {
					System.out.println(record);
					numChanges--;
				}
				nextItr = getRecordsResult.getNextShardIterator();
			}

			System.out.println("Demo complete");
		}
	}
}