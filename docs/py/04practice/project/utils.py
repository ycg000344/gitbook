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
        infer_datetime_format=True
    )

def SMA(values, n):
    return pd.Series(values).rolling(n).mean()

def crossover(series1, series2) -> bool:
    return series1[-2] < series2[-2] and series1[-1] > series2[-1]
