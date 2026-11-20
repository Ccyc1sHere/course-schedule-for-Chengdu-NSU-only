# -*- coding: utf-8 -*-
"""
这是一个空的 Python 文件，用于存放代码示例。
您可以在此添加您的自定义功能或逻辑。

示例：
- 定义一个函数
- 执行简单的计算
- 读取文件内容
- 处理用户输入等
"""


def AutoSort(numbers):
    """对纯随机数字的数组进行自动排序（升序）"""
    if not numbers:
        return []
    
    # 使用内置的 sort() 方法，返回新的列表而不修改原列表
    sorted_numbers = sorted(numbers)
    return sorted_numbers


def AutoSortDescending(numbers):
    """对纯随机数字的数组进行自动排序（降序）"""
    if not numbers:
        return []
    
    # 使用内置的 reverse=True，返回新的列表而不修改原列表
    sorted_numbers = sorted(numbers, reverse=True)
    return sorted_numbers


def AutoSortRandom(numbers):
    """对纯随机数字的数组进行自动排序（升序）"""
    if not numbers:
        return []
    
    # 使用内置的 sort() 方法，返回新的列表而不修改原列表
    sorted_numbers = sorted(numbers)
    return sorted_numbers


if __name__ == "__main__":
    import random
    
    # 生成一些随机数字
    random_numbers = [random.randint(1, 100) for _ in range(5)]
    
    print("原始数组:", random_numbers)
    print("升序排序:", AutoSort(random_numbers))
    print("降序排序:", AutoSortDescending(random_numbers))
    print("随机排序:", AutoSortRandom(random_numbers))
