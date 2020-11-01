import json
import boto3
import time


def lambda_handler(event, context):
    list_num = event["list_num"]
    with open("prefix_list_part" + str(list_num) + ".txt") as words_file:
        words = json.load(words_file)
        print(words)
        for word in words:
            time.sleep(6)
            client = boto3.client('lambda')
            response = client.invoke(
                FunctionName='KohlsScraper',
                InvocationType="Event",
                LogType="None",
                Payload= """{"toScrape": \"""" + word + "\"}"
            )
    if (event["linked"]):
        if list_num < 16:
            time.sleep(200)
            client.invoke(
                FunctionName='RunOrchestrator',
                InvocationType="Event",
                LogType="None",
                Payload= "{\"list_num\": "+ str(list_num + 1) + ",\"linked\": true}"
            )
    return {
        'statusCode': 200,
        'body': json.dumps('Hello from Lambda!')
    }
