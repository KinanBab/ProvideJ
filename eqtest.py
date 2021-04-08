import json
import sys

def json_equals(d1, d2):
  if type(d1) != type(d2): return False

  if type(d1) is list:
    if len(d1) != len(d2): return False
    for x1, x2 in zip(d1, d2):
      if not json_equals(x1, x2): return False
    return True

  elif type(d1) is dict:
    for k in d1:
      if not k in d2: return False
      if not json_equals(d1[k], d2[k]): return False

    for k in d2:
      if not k in d1: return False
    return True

  else:
    return d1 == d2

if __name__ == "__main__":
  file1 = sys.argv[1]
  file2 = sys.argv[2]
  
  print(file1)
  print(file2)
  
  with open(file1) as f1:
    with open(file2) as f2:
      d1 = json.load(f1)
      d2 = json.load(f2)
      
      if not json_equals(d1, d2):
        print("Check Error!")
        print("Input ----------------------------------------------")
        print(json.dumps(d1, indent=2))
        print("Output ----------------------------------------------")
        print(json.dumps(d2, indent=2))
        sys.exit(1)
