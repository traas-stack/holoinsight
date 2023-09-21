
const requireComponent = require.context('./technologies', false, /\.png$/);
const requireTool = require.context('./tools', false, /\.png$/);
const result: { [key: string]: string } = {};
const t: { [key: string]: string } = {};

function capitalizeFirstLetter(str: string) {
  return str.toUpperCase();
}
function validateFileName(str: string): string | undefined {
  if (/^\S+\.png$/.test(str)) {
    return str.replace(/^\S+\/(\w+)\.png$/, (rs, $1) =>
      capitalizeFirstLetter($1),
    );
  }
}
[...requireComponent.keys()].forEach((filePath: string) => {
  const componentConfig = requireComponent(filePath);

  const fileName = validateFileName(filePath);
  if (fileName) {
    result[fileName] = componentConfig;
  }
});
[...requireTool.keys()].forEach((filePath: string) => {
  const componentConfig = requireTool(filePath);

  const fileName = validateFileName(filePath);
  if (fileName) {
    t[fileName] = componentConfig;
  }
});

export default { ...result, ...t };
