const KEY = '_cutFiles';
const clipboard = {
    cutting: () =>
        // 是否有文件正被剪切
        localStorage.getItem(KEY),
    cut: file => {
        let cutFiles = localStorage.getItem(KEY);
        if (!cutFiles) {
            cutFiles = [];
        } else {
            cutFiles = JSON.parse(cutFiles);
        }
        cutFiles.push(file);

        localStorage.setItem(KEY, JSON.stringify(cutFiles));
    },
    cancelCutting: (flag) => {
        if (flag && (window.location.pathname.indexOf('log') > -1)) {
            return;
        }
        localStorage.removeItem(KEY);
    },
    listFiles: () => JSON.parse(localStorage.getItem(KEY)),
    listFileIds: () => {
        const files = JSON.parse(localStorage.getItem(KEY));
        return _.reduce(
            files,
            (p, v, k) => {
                p.push(v.id);
                return p;
            },
            [],
        );
    },
};

export default clipboard;
