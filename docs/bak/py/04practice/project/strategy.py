import abc
import numpy as np
import typing
import utils

class Strategy(metaclass=abc.ABCMeta):

    def __init__(self, broker, data):
        self._indicators = []
        self._broker = broker
        self._data = data
        self._tick = 0

    def I(self, func: typing.Callable, *args) -> np.ndarray:
        value = func(*args)
        value = np.asarray(value)
        utils.assert_msg(
            value.shape[-1] == len(self._data.Close),
            "指示器长度必须和data长度相同"
        )
        self._indicators.append(value)
        return value

    @property
    def tick(self):
        return self._tick

    @abc.abstractmethod
    def init(self):
        pass

    @abc.abstractmethod
    def next(self, tick):
        pass

    def buy(self):
        self._broker.buy()

    def sell(self):
        self._broker.sell()

    @property
    def data(self):
        return self._data

class SmaCross(Strategy):

    fast = 10
    slow = 20

    def init(self):
        self.sma1 = self.I(utils.SMA, self.data.Close, self.fast)
        self.sma2 = self.I(utils.SMA, self.data.Close, self.slow)

    def next(self, tick):
        if utils.crossover(self.sma1[:tick], self.sma2[:tick]):
            self.buy()
        elif utils.crossover(self.sma2[:tick], self.sma1[:tick]):
            self.sell()
        else:
            pass
    
