import os


class FileWrapper:
    def __init__(self, path, max_count=100):
        self.path = path
        self.file = open(path, mode='a')
        self.count = 0
        self.max_count = max_count

    def flush(self):
        self.file.flush()

    def write(self, str):
        self.file.write(str)
        self.count += 1
        if self.count > self.max_count:
            self.count = 0
            self.file.flush()
            self.file.close()
            os.rename(self.path, self.path + '.1')
            self.file = open(self.path, mode='a')

    def close(self):
        self.file.close()
