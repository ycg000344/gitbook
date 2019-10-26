import json
import ssl
import websocket

count = 5

def on_message(ws, message):
    global count
    print(message)
    count -= 1
    if count == 0:
        ws.close()


if __name__ == "__main__":
    uri = "wss://api.gemini.com/v1/marketdata/btcusd?top_of_book=true&offers=true"
    ws = websocket.WebSocketApp(uri, on_message=on_message)
    ws.run_forever(sslopt={"cert_reqs": ssl.CERT_NONE})