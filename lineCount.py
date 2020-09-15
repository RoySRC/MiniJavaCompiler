import os, subprocess
extensions = ('.java')
exclude_directories = {'syntaxtree', 'visitor', 'test'}
exclude_files ={'JavaCharStream.java', 'MiniJavaParser.java', 'MiniJavaParserConstants.java',
'MiniJavaParserTokenManager.java', 'ParseException.java', 'Token.java', 'TokenMgrError.java', 'Makefile'}


def get_files():
    candidateFiles = []
    for dname, dirs, files in os.walk('.'):  #this loop though directies recursively
        dirs[:] = [d for d in dirs if d not in exclude_directories] # exclude directory if in exclude list
        for fname in files:
            if(fname.lower().endswith(extensions) and fname not in exclude_files): #check for extension
               fpath = os.path.join(dname, fname)   #this generate full directory path for file
               candidateFiles += [fpath]
    return candidateFiles


def generate_table(candidateFiles):
    from prettytable import PrettyTable
    table = PrettyTable()
    table.field_names = ["File", "Line"]

    cumulative = 0
    for f in candidateFiles:
        cmd = "wc -l "+f
        output = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE).stdout.read().decode()
        count = output.split(' ')[0]
        table.add_row([f, count])
        cumulative += int(count)

    return table, cumulative

if __name__ == '__main__':
    table, cumulative = generate_table(get_files())
    print(table)
    print("Total number of lines:", cumulative)
