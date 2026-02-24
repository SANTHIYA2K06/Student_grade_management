import React from 'react';
import Spline from '@splinetool/react-spline';

function Home() {
  return (
    <div style={{ width: '100%', height: '90%', overflow: 'hidden' }}>
      <div style={{ width: '100%', height: '100%', clipPath: 'inset(0 160px 0 0)' }}>
        <Spline scene="https://prod.spline.design/w1PbMynmpND7a9ED/scene.splinecode" />
      </div>
    </div>
  );
}

export default Home;
