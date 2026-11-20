# -*- coding: utf-8 -*-
"""
这是一个包含100个随机纯数字的Python文件。
这些数字可以组合成一个非常大的纯数字（支持任意精度的整数）。

示例：
- 生成随机数
- 创建大整数数组
- 处理用户输入等
"""


import random

def generate_random_numbers(count=100):
    """生成指定数量的随机纯数字（可组成大整数）"""
    numbers = []
    
    # 使用random.randint()生成多个随机数，每个数都可以很大
    for _ in range(count):
        number = random.randint(1, 2**64)  # 生成一个64位的大整数
        numbers.append(number)
    
    return numbers


def create_random_100_digit_number():
    """创建一个纯随机100位的纯数字（大整数）"""
    if not numbers:
        return None
    
    # 将列表转换为字符串，组成一个非常大的纯数字
    big_number = "".join(str(num) for num in numbers)
    
    return int(big_number), len(numbers)


if __name__ == "__main__":
    import sys
    
    print("=" * 50)
    print("生成100个随机纯数字数组")
    print("=" * 50)
    
    # 生成100个随机数
    numbers = generate_random_numbers(100)
    
    print(f"\n生成的随机数字数量: {len(numbers)}")
    print(f"第一个数字: {numbers[0]}")
    print(f"最后一个数字: {numbers[-1]}")
    print(f"总和: {sum(numbers)}")
    
    # 创建大整数数组
    big_numbers, count = create_random_100_digit_number()
    
    print(f"\n生成的纯随机100位纯数字:")
    for i in range(min(5, len(big_numbers))):
        print(f"  {i+1}. {big_numbers[i]}")
    
    # 显示前几个大整数
    print("\n前几个大整数示例:")
    for i in range(min(3, len(big_numbers))):
        print(f"  {big_numbers[i]}")
