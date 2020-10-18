import json
import boto3

def lambda_handler(event, context):
    with open("words.txt") as words_file:
        words = json.load(words_file)
        print(words)
        for word in words:
            client = boto3.client('lambda')
            response = client.invoke(
                FunctionName='KohlsScraper',
                InvocationType="Event",
                LogType="None",
                Payload= """{"toScrape": \"""" + word + "\"}"
            )
    return {
        'statusCode': 200,
        'body': json.dumps('Hello from Lambda!')
    }