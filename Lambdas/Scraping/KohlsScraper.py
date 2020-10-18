import requests
import json
from bs4 import BeautifulSoup


import pymysql.cursors
import time
import re

def lambda_handler(event, context):
    print(event["toScrape"])
    scraper_configs = None
    with open("scraperConfigs.json", "r") as scraper_configs_file:
        scraper_configs = json.load(scraper_configs_file)

    headers = {
        "apikey": scraper_configs["apikey"]
        
    }

    params = (
       ("url","https://www.kohls.com/search.jsp?submit-search=web-regular&search="+ event["toScrape"]),
       ("location","na"),
    );

    response = requests.get("https://app.zenscrape.com/api/v1/get", headers=headers, params=params)

    soup = BeautifulSoup(response.text, "html.parser")

    insert_params = []

    for match in soup.find_all(id=re.compile(".*_prod_price")):
        price = None
        description = ""
        match_split = match.text.split()
        for section in match_split:
            if '$' in section:
                description = ""
                if price == None:
                    price = section
                continue
            if ('(' in section) or (')' in section):
                continue
            description += section + " "
        description = description.strip()
        imgUrl = ""
        imgUrlBase = "https://media.kohlsimg.com/is/image/kohls/"
        for prior in match.previous_siblings:
            if imgUrlBase in str(prior):
                imgUrl = imgUrlBase + str(prior).split(imgUrlBase)[1].split('?')[0].split('"')[0]
        print(price + " for: " + description + " @: " + imgUrl)
        insert_params.append((3, description, float(price.split('$')[1]), imgUrl))

    db_configs = None
    with open("dbConfigs.json", "r") as db_configs_file:
        db_configs = json.load(db_configs_file)


    connection = pymysql.connect(host=db_configs["host"],
                                 user=db_configs["user"],
                                 password=db_configs["password"],
                                 db=db_configs["db_name"],
                                 charset='utf8mb4',
                                 cursorclass=pymysql.cursors.DictCursor)

    try:
        with connection.cursor() as cursor:
            PRODUCT_INSERT_SYNTAX = "INSERT INTO Product (chainID, description, price, imageURL) VALUES (%s, %s, %s, %s);"
            cursor.executemany(PRODUCT_INSERT_SYNTAX, insert_params)
            connection.commit()
    except Exception as e:
        print(e)
        traceback.print_exc()
    finally:
        connection.close()

    return {
        'statusCode': 200,
        'body': 'Scraped: ' + event["toScrape"]
    }
