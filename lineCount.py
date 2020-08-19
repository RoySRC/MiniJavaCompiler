import os, subprocess
extensions = ('.java')                                    #extinctions want to search
exclude_directories = ['PrettyPrintVisitor', 'syntaxtree', 'visitor', 'test'] 
exclude_files = ['JavaCharStream.java', 'MiniJavaParser.java', 'MiniJavaParserConstants.java',
'MiniJavaParserTokenManager.java', 'ParseException.java', 'Token.java', 'TokenMgrError.java', 'Makefile']
exclude_directories = set(exclude_directories)
candidateFiles = []
for dname, dirs, files in os.walk('.'):  #this loop though directies recursively 
    dirs[:] = [d for d in dirs if d not in exclude_directories] # exclude directory if in exclude list 
    for fname in files:
        if(fname.lower().endswith(extensions) and fname not in exclude_files): #check for extension 
           fpath = os.path.join(dname, fname)   #this generate full directory path for file
           candidateFiles += [fpath]

_LINE_ = "============================================================================================="
cumulative = 0
print(_LINE_)
print("LINE => FILE")
print(_LINE_)
for f in candidateFiles:
    cmd = "wc -l "+f
    output = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE).stdout.read().decode()
    count = output.split(' ')[0]
    print(count, "=>", f)
    cumulative += int(count)
print(_LINE_)
print("Total number of lines:", cumulative)

