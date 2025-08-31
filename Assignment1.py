import math
from functools import reduce
import re
from collections import Counter

with open('input.txt', 'r') as file:
    ip_str = str(file.read().strip())

ip_str = str(ip_str)
s_nospace = ip_str
s_nospace = s_nospace.replace(" ", "")
s_nospace = re.sub(r'[^A-Za-z]', '', s_nospace)


substrings = set()
repeated_substring = []
possible_key_length = []

for i in range(0, len(s_nospace)):
    for j in range(i, len(s_nospace)):
        s = s_nospace[i:j + 1]
        if (len(s) > 1):
            if (s in substrings):
                if (s not in repeated_substring):
                    repeated_substring.append(s)
            else:
                substrings.add(s)

if (len(repeated_substring) == 0):
    possible_key_length.append(2)
    possible_key_length.append(3)
    possible_key_length.append(4)
    possible_key_length.append(5)

gap = []

for i in repeated_substring:
    gap_i = []
    count = -1
    prev = -1
    next = -1
    for j in s_nospace:
        count += 1
        if j == i[0]:
            look_str = s_nospace[count:count + len(i)]

            if look_str == i:
                if prev == -1:
                    prev = count
                elif next == -1:
                    next = count
                    gap_i.append(next - prev)
                else:
                    prev = next
                    next = count
                    gap_i.append(next - prev)

    x = reduce(math.gcd, gap_i)
    gap.append(x)

key2_count = 0
key3_count = 0
key4_count = 0
key5_count = 0
key_length = 0
if (len(gap) != 0):
    for i in gap:
        if i % 2 == 0:
            key2_count += 1
        if i % 3 == 0:
            key3_count += 1
        if i % 4 == 0:
            key4_count += 1
        if i % 5 == 0:
            key5_count += 1

    key_domain = {'3': key3_count, '4': key4_count, '5': key5_count}
    key_length = int(max(key_domain, key=key_domain.get))
    possible_key_length.append(key_length)
    key_count = key_domain.get(str(key_length))
    if (key2_count >= key_count):
        possible_key_length.append(2)

    if (key_length == 3):
        if (key3_count == key5_count):
            possible_key_length.append(5)
        if (key3_count == key4_count):
            possible_key_length.append(4)
            possible_key_length.append(2)

    elif (key_length == 4):
        if 2 not in possible_key_length:
            possible_key_length.append(2)
        if (key4_count == key5_count):
            possible_key_length.append(5)

possible_key_length.sort(reverse=True)
final_key_length = 0
ic_four = 0
ic_two = 0
closest_key_length = 0
last_final_ic = 0

for i in possible_key_length:
    l0 = []
    l1 = []
    l2 = []
    l3 = []
    l4 = []
    count = -1

    # Filling all the lists
    for j in s_nospace:
        count += 1
        if (count % i == 0):
            l0.append(j)
        elif (count % i == 1):
            l1.append(j)
        elif (count % i == 2):
            l2.append(j)
        elif (count % i == 3):
            l3.append(j)
        elif (count % i == 4):
            l4.append(j)

    lst = [l0, l1, l2, l3, l4]
    total_ic = 0
    for j in lst:
        n = len(j)
        if (n > 1):
            frequency = Counter(j)
            freq_sum = 0
            for element, freq in frequency.items():
                freq_sum += freq * (freq - 1)
            ic = float(freq_sum / (n * (n - 1)))
            total_ic += ic

    final_ic = float(total_ic / i)
    final_ic = int(final_ic * 100) / 100

    # In case if final_key_length == 0 , we will consider the nearest ic
    close = min(final_ic, last_final_ic, key=lambda x: abs(x - 0.06))
    # print(close)
    if (close == final_ic):
        closest_key_length = i
    # print(closest_key_length)
    last_final_ic = final_ic
    # print(final_ic)
    if (i == 2):
        ic_two = final_ic
    if (i == 4):
        ic_four = final_ic

    if (final_ic == 0.06):
        if (i == 4):
            continue
        final_key_length = i
        break
if (final_key_length == 0):
    if (ic_two == 0.06):
        final_key_length = 2
    elif (ic_four == 0.06):
        final_key_length = 4
    else:
        final_key_length = closest_key_length


# Satej part

from collections import Counter

key_length = final_key_length
ciphertext_original = ip_str

alphabet_position = {
    "A": 0, "B": 1, "C": 2, "D": 3, "E": 4, "F": 5, "G": 6, "H": 7, "I": 8, "J": 9,
    "K": 10, "L": 11, "M": 12, "N": 13, "O": 14, "P": 15, "Q": 16, "R": 17, "S": 18, "T": 19,
    "U": 20, "V": 21, "W": 22, "X": 23, "Y": 24, "Z": 25,
    "a": 0, "b": 1, "c": 2, "d": 3, "e": 4, "f": 5, "g": 6, "h": 7, "i": 8, "j": 9,
    "k": 10, "l": 11, "m": 12, "n": 13, "o": 14, "p": 15, "q": 16, "r": 17, "s": 18, "t": 19,
    "u": 20, "v": 21, "w": 22, "x": 23, "y": 24, "z": 25,
    0: "A", 1: "B", 2: "C", 3: "D", 4: "E", 5: "F", 6: "G", 7: "H", 8: "I", 9: "J",
    10: "K", 11: "L", 12: "M", 13: "N", 14: "O", 15: "P", 16: "Q", 17: "R", 18: "S", 19: "T",
    20: "U", 21: "V", 22: "W", 23: "X", 24: "Y", 25: "Z"
}
english_letter_frequency = [
    0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015, 0.06094, 0.06966, 0.00153,
    0.00772, 0.04025, 0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056,
    0.02758, 0.00978, 0.02360, 0.00150, 0.01974, 0.00074
]

ciphertext = ""
for temp1 in ciphertext_original:
    if temp1.isalpha():
        ciphertext = ciphertext + str(temp1)
total_alphabets_in_ciphertext = len(ciphertext)


def shift_text(text_param, shift):
    shifted_text = ""
    for i in range(len(text_param)):
        letter = text_param[i]
        curr_pos = alphabet_position[letter]
        new_pos = curr_pos + shift
        new_pos = new_pos % 26
        shifted_text = shifted_text + str(alphabet_position[new_pos])
    return shifted_text


def get_mic(text_param):
    text_len = len(text_param)
    if text_len == 0:
        return 0

    segment_freq = Counter(text_param)
    mic = 0
    for char, count in segment_freq.items():
        index = ord(char) - ord('A')
        mic += (count / text_len) * english_letter_frequency[index]
    return mic


def find_desired_shift(ciphertext_param):
    global key_length
    desired_plaintext_shifted = []
    desired_mic_shifts_combined = []
    cypher_text_divided_into_key_length = []
    cyphertext_divided_as_string = []
    for i in range(key_length):
        cypher_text_divided_into_key_length.append([])
    for i in range(len(ciphertext_param)):
        cypher_text_divided_into_key_length[i % key_length].append(ciphertext_param[i])

    for i in cypher_text_divided_into_key_length:
        x = ""
        for ele in i:
            x = x + str(ele)
        cyphertext_divided_as_string.append(x)

        allmic = []
        for shift_index in range(26):
            x_processed = x.upper()
            shifted_text = shift_text(x_processed, shift_index)
            mic = get_mic(shifted_text)
            allmic.append(mic)

        minimum_relative_mic_value = 1000
        desired_mic_shift = 0

        for mic_index in range(len(allmic)):
            mic = allmic[mic_index]
            rel_mic = abs(mic - 0.065)
            if rel_mic < minimum_relative_mic_value:
                minimum_relative_mic_value = rel_mic
                desired_mic_shift = mic_index

        Plaintext = shift_text(x, desired_mic_shift)
        desired_plaintext_shifted.append(Plaintext)
        desired_mic_shifts_combined.append(desired_mic_shift)

    return desired_mic_shifts_combined, desired_plaintext_shifted, cyphertext_divided_as_string


def merge_plaintext(plaintext_divided_into_parts_param, total_alphabets_in_plaintext):
    plaintext_merged = ""

    for i in range(total_alphabets_in_plaintext):
        divided_part = i % key_length
        index_in_part = int(i / key_length)
        plaintext_merged = plaintext_merged + plaintext_divided_into_parts_param[divided_part][index_in_part]

    return plaintext_merged


def getFormatedPlainText(generated_plaintext_p):
    global ciphertext_original
    ciphertext_original_p = ciphertext_original
    formated_plaintext_param = ""
    counter = 0
    for i in ciphertext_original_p:
        if i.isalpha():
            if i.isupper():
                formated_plaintext_param = formated_plaintext_param + str(generated_plaintext_p[counter].upper())
            else:
                formated_plaintext_param = formated_plaintext_param + str(generated_plaintext_p[counter].lower())

            counter += 1
        else:
            formated_plaintext_param = formated_plaintext_param + str(i)

    return formated_plaintext_param


def get_key(plaintext_divided_into_parts_param, cyphertext_divided_into_parts_param):
    key_param = ""
    partial_key = []

    for i in range(len(plaintext_divided_into_parts_param)):
        partial_key.append((plaintext_divided_into_parts_param[i][0], cyphertext_divided_into_parts_param[i][0]))

    for i in partial_key:
        pt_value = alphabet_position[i[0]]
        ct_value = alphabet_position[i[1]]
        final_value = (ct_value - pt_value + 26) % 26
        key_param = key_param + str(alphabet_position[final_value])

    return key_param


desired_shifts_arr, plaintext_divided_into_parts, cyphertext_divided_into_parts = find_desired_shift(ciphertext)

generated_plaintext = merge_plaintext(plaintext_divided_into_parts, total_alphabets_in_ciphertext)

formated_plain_text = getFormatedPlainText(generated_plaintext)

key = get_key(plaintext_divided_into_parts, cyphertext_divided_into_parts)





print("Plaintext: ", formated_plain_text)
print()
print("Key:", key)

print()
input("Press Enter To Exit")

