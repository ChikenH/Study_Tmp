# coding: utf-8

'''
 標準入力の空行を削除する
'''
import sys

# 複数行の標準入力をリスト化
def inputToList():
    listInput = []

    # 標準入力を 1 行ずつリストに追加
    for line in sys.stdin:
        listInput.append(line)

    return listInput

# リスト内の空行を除外する
def deleteBlankLine(lists, strCode):
    strRtn = ""

    for strBuf in lists:
        # 空行はとばす
        if strBuf == strCode:
            continue

        strRtn += strBuf

    return strRtn

# リスト化された標準入力を取得
listInputs = inputToList()
# print(listInputs)

# 改行コード
strLFCode = "\n"

# 整理後の文を出力
strOutputs = deleteBlankLine(listInputs, strLFCode)
print(strOutputs)
