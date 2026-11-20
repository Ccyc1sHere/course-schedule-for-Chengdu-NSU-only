import random


def generate_random_numbers(count=100):
    numbers = []
    for _ in range(count):
        number = random.randint(1, 2**64)
        numbers.append(number)
    return numbers


def create_random_100_digit_number():
    if not generate_random_numbers(1):
        return None
    
    big_number = "".join(str(num) for num in generate_random_numbers(1))
    
    return int(big_number), len(generate_random_numbers(1))


def sort_array(numbers):
    if not numbers:
        return []
    
    sorted_numbers = sorted(numbers)
    return sorted_numbers


if __name__ == "__main__":
    import sys
    
    print("=" * 50)
    print("生成的随机数字数组:")
    for i, num in enumerate(generate_random_numbers(10)):
        print(f"  {i+1}. {num}")
    
    sorted_asc = sort_array(generate_random_numbers(10))
    print("\n--- 升序排序结果 ---")
    for i, num in enumerate(sorted_asc):
        print(f"  {i+1}. {num}")
    
    new_numbers = generate_random_numbers(5)
    
    print("\n--- 新的大整数数组 ---")
    for i, num in enumerate(new_numbers):
        print(f"  {i+1}. {num}")
    
    big_number, count = create_random_100_digit_number()
    
    print("\n--- 生成的纯随机 100 位纯数字 ---")
    for i in range(min(3, len(big_number))):
        print(f"  {i+1}. {big_number[i]}")
