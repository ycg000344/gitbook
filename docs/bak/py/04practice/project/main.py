import utils
from strategy import SmaCross
from backtest import Backtest, ExchangeAPI

def main():
    BUCSD = utils.read_file("BTCUSD_GEMINI.csv")
    ret = Backtest(BUCSD, SmaCross, ExchangeAPI, 10000.0, 0.003).run()
    print(ret)

if __name__ == "__main__":
    main()