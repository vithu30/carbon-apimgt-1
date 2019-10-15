/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useMemo } from 'react';
import PropTypes from 'prop-types';
import { useDropzone } from 'react-dropzone';
import DeleteIcon from '@material-ui/icons/Delete';
import { Button } from '@material-ui/core/';

/**
 *
 * Convert raw byte values to human readable format
 * @param {Number} bytes number of bytes
 * @param {boolean} [si=false]
 * @returns {String} Human readable string format
 */
function humanFileSize(bytesParam, si = false) {
    let bytes = bytesParam; // To prevent `no-param-reassign` eslint rule violation
    const thresh = si ? 1000 : 1024;
    if (Math.abs(bytes) < thresh) {
        return bytes + ' B';
    }
    const units = si
        ? ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
        : ['KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB'];
    let u = -1;
    do {
        bytes /= thresh;
        ++u;
    } while (Math.abs(bytes) >= thresh && u < units.length - 1);
    return bytes.toFixed(1) + ' ' + units[u];
}

const baseStyle = {
    flex: 1,
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    padding: '20px',
    borderWidth: 2,
    borderRadius: 2,
    borderColor: '#eeeeee',
    borderStyle: 'dashed',
    backgroundColor: '#fafafa',
    color: '#bdbdbd',
    outline: 'none',
    transition: 'border .24s ease-in-out',
};

const activeStyle = {
    borderColor: '#2196f3',
};

const acceptStyle = {
    borderColor: '#00e676',
};

const rejectStyle = {
    borderColor: '#ff1744',
};

/**
 *
 * Provide a class friendly Dropzone wrapper using hooks.
 * @export
 * @returns
 */
export default function DropZoneLocal(props) {
    let { files } = props;
    const {
        message, onDrop, error, showFilesList, children,
    } = props;
    files = files instanceof File ? [files] : files;
    const dropZoneObject = useDropzone({ onDrop });
    const {
        getRootProps, getInputProps, isDragActive, isDragAccept, isDragReject,
    } = dropZoneObject;
    let { acceptedFiles } = dropZoneObject;
    acceptedFiles = files || acceptedFiles;
    const removeFile = file => {
        acceptedFiles.splice(acceptedFiles.indexOf(file), 1);
        onDrop(acceptedFiles);
    };
    const filesList = acceptedFiles.map(file => (
        <li key={file.path}>
            {file.path} - {humanFileSize(file.size)}
            <Button onClick={removeFile}><DeleteIcon/></Button>
        </li>
    ));
    baseStyle.borderColor = error ? rejectStyle.borderColor : '#eeeeee';
    const style = useMemo(
        () => ({
            ...baseStyle,
            ...(isDragActive ? activeStyle : {}),
            ...(isDragAccept ? acceptStyle : {}),
            ...(isDragReject ? rejectStyle : {}),
        }),
        [isDragActive, isDragReject, error],
    );
    return (
        <section className='container'>
            <div {...getRootProps({ style })}>
                <input {...getInputProps()} />
                {children || <p>{message}</p>}
            </div>
            {showFilesList && (
                <aside>
                    <h4>Files</h4>
                    <ul>{filesList}</ul>
                </aside>
            )}
        </section>
    );
}
DropZoneLocal.defaultProps = {
    message: "Drag 'n' drop some files here, or click to select files",
    onDrop: () => {},
    showFilesList: true,
    files: null,
    children: null,
    error: false,
};
DropZoneLocal.propTypes = {
    message: PropTypes.string,
    onDrop: PropTypes.func,
    showFilesList: PropTypes.bool,
    files: PropTypes.oneOfType([PropTypes.arrayOf(PropTypes.object), PropTypes.instanceOf(File)]),
    children: PropTypes.oneOfType([PropTypes.element, PropTypes.array]),
    error: PropTypes.oneOfType([PropTypes.bool, PropTypes.shape({})]),
};
