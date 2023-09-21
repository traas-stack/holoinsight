import { queryMetricLike } from '@/services/tenant/api';
import { useRequest } from 'ahooks';
import { Spin } from 'antd';
import { promLanguageDefinition } from 'monaco-promql';
import React, { useEffect, useRef } from 'react';
import MonacoEditor from 'react-monaco-editor';

interface Props {
  value?: string;
  width?: number | string;
  height?: number | string;
  onChange?: (value: string) => void;
  readOnly?: boolean;
}
interface EditorProps extends Props {
  metrics: string[];
}
function Editor(props: EditorProps) {
  const monacoProviderRef = useRef<any>(null);
  const monacoRef = useRef<any>(null);
  function editorWillMount(monaco: any) {
    const languageId = promLanguageDefinition.id;
    monacoRef.current = monaco;
    monaco.languages.register(promLanguageDefinition);
    monaco.languages.onLanguage(languageId, () => {
      promLanguageDefinition.loader().then((mod) => {
        monaco.languages.setMonarchTokensProvider(languageId, mod.language);
        monaco.languages.setLanguageConfiguration(
          languageId,
          mod.languageConfiguration,
        );
        monaco.languages.registerCompletionItemProvider(
          languageId,
          mod.completionItemProvider,
        );
      });
    });
  }
  function handleChange(v: string) {
    props.onChange?.(v);
  }
  function loadMetrics(metrics: string[]) {
    if (metrics.length > 0) {
      monacoProviderRef.current =
        monacoRef.current.languages.registerCompletionItemProvider(
          promLanguageDefinition.id,
          {
            // @ts-ignore
            provideCompletionItems: function (model, position, context) {
              let suggestions: any = [];
              metrics.forEach((item, index) => {
                suggestions.push({
                  label: item,
                  insertText: item,
                  kind: 13,
                  detail: 'Metric',
                });
              });
              return { suggestions };
            },
          },
        );
    }
  }
  useEffect(() => {
    if (props?.metrics?.length) {
      loadMetrics(props.metrics);
    }
    return () => {
      monacoProviderRef.current?.dispose?.();
      monacoRef.current?.dispose?.();
    };
  }, []);
  return (
    <MonacoEditor
      width={props.width}
      height={props.height}
      language="promql"
      theme="vs-dark"
      value={props.value}
      options={{
        readOnly: props.readOnly,
        minimap: {
          enabled: false,
        },
      }}
      onChange={handleChange}
      editorWillMount={editorWillMount}
    />
  );
}
export default function Index(props: Props) {
  const { loading, data = [] } = useRequest(queryMetricLike, {
    defaultParams: [''],
  });

  return (
    <Spin spinning={loading}>
      {!loading && <Editor {...props} metrics={data as string[]} />}
    </Spin>
  );
}
