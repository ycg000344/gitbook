import os.path as path
import pandas as pd

def assert_msg(condition, msg):
    if not condition:
        raise Exception(msg)

def read_file(filename):
    filepath = path.join(path.dirname(__file__), filename)
    assert_msg(path.exists(filepath), "文件不存在")
    return pd.read_csv(
        filepath,
        index_col=0,
        parse_dates=True,
        infer_datetime_format=True)

if __name__ == "__main__":
    BTCUSD = read_file("BTCUSD_GEMINI.csv")
    assert_msg(BTCUSD.__len__() > 0, "读取失败")
    print(BTCUSD.head())
