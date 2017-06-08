import importlib
#import moduleA
i = importlib.import_module("moduleA")

method = getattr(i,'plus')

method(1,2)


