import _thread
import time
import websocket

def on_message(ws, message):
    print("receive :{}".format(message))

def on_open(ws):
    def go():
        for i in range(5):
            time.sleep(0.01)
            message = "{0}".format(i)
            ws.send(message)
            print("send: {}".format(message))

        time.sleep(1)
        ws.close()
        print("ws close.")
    _thread.start_new_thread(go, ())

if __name__ == "__main__":
    ws = websocket.WebSocketApp("ws://echo.websocket.org/",on_message=on_message, on_open=on_open)
    ws.run_forever()