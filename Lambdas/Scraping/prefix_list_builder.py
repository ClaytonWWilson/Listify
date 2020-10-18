wordlist = []
with open("nounlist.txt") as nounlist:
  for noun in nounlist:
    wordlist.append(noun)

prefix_list = []
for word in wordlist:
  prefix_list.append(word[:min(len(word), 3)])

short_list = []
short_list2 = []
for prefix in prefix_list:
  prefix = prefix.strip()
  if len(short_list) < 700:
    if (len(short_list) == 0 or short_list[-1] != prefix):
      short_list.append(prefix)
  else:
    if ((len(short_list2) == 0 or short_list2[-1] != prefix) and short_list[-1] != prefix):
      short_list2.append(prefix)

with open("prefix_list_part1.txt", "w") as prefix_list_part1:
  json.dump(short_list, prefix_list_part1)

with open("prefix_list_part2.txt", "w") as prefix_list_part2:
  json.dump(short_list2, prefix_list_part2)