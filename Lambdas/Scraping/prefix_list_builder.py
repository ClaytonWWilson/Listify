import json

LIST_SIZE = 100

wordlist = []
with open("nounlist.txt") as nounlist:
  for noun in nounlist:
    wordlist.append(noun)

prefix_list = []
for word in wordlist:
  prefix_list.append(word[:min(len(word), 3)])

word_lists = []
for i in range(int(len(prefix_list) / LIST_SIZE)):
  word_lists.append([])

current_list_len = 0
current_list = 0
for prefix in prefix_list:
  prefix = prefix.strip()
  if current_list_len >= LIST_SIZE:
    if (word_lists[current_list][-1] != prefix):
      current_list_len = 0
      current_list += 1
  if (current_list_len == 0 or word_lists[current_list][-1] != prefix):
    word_lists[current_list].append(prefix)
    current_list_len += 1

for i in range(current_list + 1):
  with open("prefix_list_part" + str(i + 1) + ".txt", "w") as prefix_list_part:
    json.dump(word_lists[i], prefix_list_part)

