import requests

headers = { 
    "apikey": ""
}

params = (
   ("url","https://www.kohls.com/search.jsp?submit-search=web-regular&search=shoes"),
   ("location","na"),
);

response = requests.get('https://app.zenscrape.com/api/v1/get', headers=headers, params=params);
print(response.text)

soup = BeautifulSoup(response.text, 'html.parser')

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
   imgUrlBase = 'https://media.kohlsimg.com/is/image/kohls/'
   for prior in match.previous_siblings:
     if imgUrlBase in str(prior):
       imgUrl = imgUrlBase + str(prior).split(imgUrlBase)[1].split('?')[0].split('"')[0]
   print(price + " for: " + description + " @: " + imgUrl)

