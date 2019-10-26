import numpy as np
import pandas as pd
import numbers

import utils
from strategy import Strategy

class ExchangeAPI:

    def __init__(self, data, cash, commission):
        utils.assert_msg(0 < cash, "初始资金大于0，输入的现金数量：{}".format(cash))
        utils.assert_msg(0 <= commission <= 0.05, "合理的手续费不超过5%，输入的费率：{}".format(commission))

        self._init_cash = cash
        self._data = data
        self._commission = commission
        self._position = 0
        self._cash = cash
        self._i = 0

    @property
    def cash(self):
        return self._cash

    @property
    def position(self):
        return self._position
    
    @property
    def inital_cash(self):
        return self._init_cash

    @property
    def market_value(self):
        return self._cash + self._position * self.current_price

    @property
    def current_price(self):
        return self._data.Close[self._i]

    def buy(self):
        self._position = float(self._cash / (self.current_price * (1 + self._commission)))
        self._cash = 0.0

    def sell(self):
        self._cash += float(self._position * self.current_price * (1 - self._commission))
        self._position = 0.0

    def next(self, tick):
        self._i = tick


class Backtest:
    def __init__(
        self,
        data: pd.DataFrame,
        strategy_type: type(Strategy),
        broker_type: type(ExchangeAPI),
        cash: float = 1000,
        commission: float = .0):

        utils.assert_msg(issubclass(strategy_type, Strategy), "strategy_type 不是一个Strategy类型")
        utils.assert_msg(issubclass(broker_type, ExchangeAPI), "broker_type 不是一个ExchangeAPI类型")
        utils.assert_msg(isinstance(commission, numbers.Number), "commission 不是浮点型")
        
        data = data.copy(False)
        if "Volume" not in data:
            data["Volume"] = np.nan

        utils.assert_msg(
            len(data.columns & {
                "Open", "High", "Low", "Close", "Volume"
            }) == 5,
            "输入的 `data` 格式不正确"
        )

        utils.assert_msg(
            not data[
                ["Open", "High", "Low", "Close", "Volume"]
            ].max().isnull().any(),
            "部分 OHLC 包含缺失值 "
        )

        if not data.index.is_monotonic_increasing:
            data = data.sort_index()

        self._data = data
        self._broker = broker_type(data, cash, commission)
        self._strategy = strategy_type(self._broker, self._data)
        self._results = None

    def run(self):
        strategy = self._strategy
        broker = self._broker

        strategy.init()

        start = 100
        end = len(self._data)

        for i in range(start, end):
            broker.next(i)
            strategy.next(i)

        self._results = self._compute_result(broker)
        return self._results

    def _compute_result(self, broker):
        s = pd.Series()
        s["初始市值"] = broker.inital_cash
        s["结束市值"] = broker.market_value
        s["收益"] = broker.market_value - broker.inital_cash
        return s 