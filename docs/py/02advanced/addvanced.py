
import asyncio
import random


def methon_use_time(func):
    import time
    import functools
    @functools.wraps(func)
    def wrapper(*args, **kwargs):
        start = time.perf_counter()
        res = func(*args, **kwargs)
        print('func：{} use time: {}ms'.format(
            func.__name__, (time.perf_counter() - start) * 1000))
        return res
    return wrapper


async def crawl_page(url):
    sleep_time = random.randint(1, 10)
    print('url: {}, sleep_time: {}'.format(url, sleep_time))
    await asyncio.sleep(sleep_time)
    print('ok, {}'.format(url))

# stage 1
# async def main(urls):
#     for url in urls:
#         await crawl_page(url)

# stage 2

# @methon_use_time
# async def main(urls):
#     tasks = [asyncio.create_task(crawl_page(url)) for url in urls]
#     for task in tasks:
#         await task


# stage 3
async def main(urls):
    await asyncio.gather(*[asyncio.create_task(crawl_page(url)) for url in urls])


if __name__ == "__main__":
    asyncio.run(main(['url_1', 'url_2', 'url_3']))
