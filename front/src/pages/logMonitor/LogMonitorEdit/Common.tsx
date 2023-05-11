

export const findIndexN = (str: string, lefts: string, num: number) => {
    let x = str.indexOf(lefts);
    for (let i = 0; i < num - 1; i++) {
        x = str.indexOf(lefts, x + lefts.length);
    }
    return x;
};

// 处理LR切分
export const handleLRColumnValue = (params?: any, log?: string) => {
    const { leftIndex, left, right } = params;
    const sample = log ||  '';
    if (left === '' && right !== '' && leftIndex === 0)
        return sample.substr(0, sample.indexOf(right));
    if (left === '' && right === '' && leftIndex === 0) return sample;
    if (left === '' && right === '' && leftIndex !== 0) return '';

    const index = findIndexN(sample, left, leftIndex);

    if (index === -1) return '';
    const subStr = sample.substr(index + (left || '').length);
    if (left !== '' && right === '') return subStr;
    const i = subStr.indexOf(right);
    return subStr.substr(0, i);
};